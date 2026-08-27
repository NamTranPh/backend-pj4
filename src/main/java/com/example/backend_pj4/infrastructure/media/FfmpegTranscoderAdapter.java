package com.example.backend_pj4.infrastructure.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.application.port.out.VideoTranscoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FfmpegTranscoderAdapter implements VideoTranscoder {

    private final FileStorageService fileStorageService;

    public FfmpegTranscoderAdapter(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TranscodeResult transcode(TranscodeCommand command) {
        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("transcode-" + command.contentId());
            Path rawFile = workDir.resolve("input.mp4");

            log.info("Downloading raw video: bucket={}, key={}", command.rawBucket(), command.rawFileKey());
            try (InputStream is = fileStorageService.getObject(command.rawBucket(), command.rawFileKey())) {
                Files.copy(is, rawFile);
            }

            int duration = probeDuration(rawFile);
            int sourceHeight = probeHeight(rawFile);

            List<Resolution> resolutions = determineResolutions(sourceHeight);
            log.info("Transcoding contentId={} resolutions={}", command.contentId(), resolutions);

            Path outputDir = workDir.resolve("hls");
            Files.createDirectories(outputDir);

            runFfmpeg(rawFile, outputDir, resolutions);

            String masterKey = uploadHlsFiles(outputDir, command.outputBucket(), command.outputPrefix());

            String resolutionStr = String.join(",",
                    resolutions.stream().map(r -> r.label).toList());

            log.info("Transcode complete contentId={} masterKey={}", command.contentId(), masterKey);
            return new TranscodeResult(masterKey, resolutionStr, duration);

        } catch (Exception e) {
            log.error("Transcode failed for contentId={}", command.contentId(), e);
            throw new RuntimeException("Transcode failed", e);
        } finally {
            if (workDir != null) {
                cleanupDir(workDir);
            }
        }
    }

    private void runFfmpeg(Path input, Path outputDir, List<Resolution> resolutions) throws Exception {
        for (Resolution r : resolutions) {
            Path variantDir = outputDir.resolve(r.label);
            Files.createDirectories(variantDir);

            List<String> cmd = List.of(
                    "ffmpeg", "-i", input.toString(), "-y",
                    "-vf", "scale=-2:" + r.height,
                    "-c:v", "libx264", "-b:v", r.bitrate,
                    "-c:a", "aac", "-b:a", "128k",
                    "-hls_time", "6", "-hls_list_size", "0",
                    "-hls_segment_filename", variantDir.resolve("segment_%03d.ts").toString(),
                    "-f", "hls", variantDir.resolve("playlist.m3u8").toString()
            );

            log.info("Running ffmpeg for {}: {}", r.label, String.join(" ", cmd));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("ffmpeg [{}]: {}", r.label, line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("ffmpeg exited with code " + exitCode + " for " + r.label);
            }
        }

        StringBuilder masterPlaylist = new StringBuilder("#EXTM3U\n");
        for (Resolution r : resolutions) {
            masterPlaylist.append(String.format(
                    "#EXT-X-STREAM-INF:BANDWIDTH=%d,RESOLUTION=%dx%d\n%s/playlist.m3u8\n",
                    r.bandwidthBps, r.width, r.height, r.label));
        }
        Files.writeString(outputDir.resolve("master.m3u8"), masterPlaylist.toString());
    }

    private String uploadHlsFiles(Path outputDir, String bucket, String prefix) throws Exception {
        try (Stream<Path> paths = Files.walk(outputDir)) {
            List<Path> files = paths.filter(Files::isRegularFile).toList();
            for (Path file : files) {
                String relativePath = outputDir.relativize(file).toString().replace("\\", "/");
                String objectKey = prefix + "/" + relativePath;
                String contentType = relativePath.endsWith(".m3u8")
                        ? "application/vnd.apple.mpegurl"
                        : "video/mp2t";

                int lastSlash = objectKey.lastIndexOf('/');
                String folder = lastSlash > 0 ? objectKey.substring(0, lastSlash) : "";
                String filename = lastSlash > 0 ? objectKey.substring(lastSlash + 1) : objectKey;

                try (FileInputStream fis = new FileInputStream(file.toFile())) {
                    fileStorageService.upload(bucket, folder, filename, fis, file.toFile().length(), contentType);
                }
            }
        }
        return prefix + "/master.m3u8";
    }

    private int probeDuration(Path file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1", file.toString());
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            return (int) Double.parseDouble(output);
        } catch (Exception e) {
            log.warn("Failed to probe duration, defaulting to 0", e);
            return 0;
        }
    }

    private int probeHeight(Path file) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-select_streams", "v:0",
                    "-show_entries", "stream=height",
                    "-of", "default=noprint_wrappers=1:nokey=1", file.toString());
            Process process = pb.start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            return Integer.parseInt(output);
        } catch (Exception e) {
            log.warn("Failed to probe height, defaulting to 720", e);
            return 720;
        }
    }

    private List<Resolution> determineResolutions(int sourceHeight) {
        List<Resolution> available = List.of(
                new Resolution("480p", 854, 480, "1400k", 1_500_000),
                new Resolution("720p", 1280, 720, "2800k", 3_000_000),
                new Resolution("1080p", 1920, 1080, "5000k", 5_500_000)
        );
        return available.stream()
                .filter(r -> r.height <= sourceHeight)
                .toList()
                .isEmpty()
                        ? List.of(available.get(0))
                        : available.stream().filter(r -> r.height <= sourceHeight).toList();
    }

    private void cleanupDir(Path dir) {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
            log.warn("Failed to cleanup temp dir: {}", dir, e);
        }
    }

    private record Resolution(String label, int width, int height, String bitrate, int bandwidthBps) {}
}
