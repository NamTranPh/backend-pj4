package com.example.backend_pj4.application.port.out;

public interface VideoTranscoder {

    TranscodeResult transcode(TranscodeCommand command);

    record TranscodeCommand(
            String contentId,
            String rawBucket,
            String rawFileKey,
            String outputBucket,
            String outputPrefix
    ) {}

    record TranscodeResult(
            String masterPlaylistKey,
            String resolutions,
            Integer durationSeconds
    ) {}
}
