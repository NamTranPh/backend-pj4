package com.example.backend_pj4.presentation.episode;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend_pj4.application.command.episode.CreateEpisodeCommand;
import com.example.backend_pj4.application.command.episode.UpdateEpisodeCommand;
import com.example.backend_pj4.application.command.movie.RecordUploadPartCommand;
import com.example.backend_pj4.application.command.movie.StartUploadCommand;
import com.example.backend_pj4.application.dto.episode.EpisodeResult;
import com.example.backend_pj4.application.dto.movie.PresignedUrlResult;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;
import com.example.backend_pj4.application.port.in.episode.CreateEpisodeUseCase;
import com.example.backend_pj4.application.port.in.episode.DeleteEpisodeUseCase;
import com.example.backend_pj4.application.port.in.episode.ListEpisodesByMovieUseCase;
import com.example.backend_pj4.application.port.in.episode.UpdateEpisodeUseCase;
import com.example.backend_pj4.application.port.in.episode.UploadEpisodeThumbnailUseCase;
import com.example.backend_pj4.application.port.in.movie.CancelUploadUseCase;
import com.example.backend_pj4.application.port.in.movie.CompleteUploadUseCase;
import com.example.backend_pj4.application.port.in.movie.GetPartUrlsUseCase;
import com.example.backend_pj4.application.port.in.movie.GetUploadStatusUseCase;
import com.example.backend_pj4.application.port.in.movie.RecordUploadPartUseCase;
import com.example.backend_pj4.application.port.in.movie.StartUploadUseCase;
import com.example.backend_pj4.presentation.episode.request.CreateEpisodeRequest;
import com.example.backend_pj4.presentation.episode.request.UpdateEpisodeRequest;
import com.example.backend_pj4.presentation.movie.request.GetPartUrlsRequest;
import com.example.backend_pj4.presentation.movie.request.RecordPartRequest;
import com.example.backend_pj4.presentation.movie.request.StartUploadRequest;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Episodes - Admin")
@AuthRequired
@RestController
@RequestMapping("/api/v1/admin/movies/{movieId}/episodes")
public class AdminEpisodeController {

    private final CreateEpisodeUseCase createEpisodeUseCase;
    private final UpdateEpisodeUseCase updateEpisodeUseCase;
    private final ListEpisodesByMovieUseCase listEpisodesByMovieUseCase;
    private final DeleteEpisodeUseCase deleteEpisodeUseCase;
    private final UploadEpisodeThumbnailUseCase uploadEpisodeThumbnailUseCase;
    private final StartUploadUseCase startUploadUseCase;
    private final GetPartUrlsUseCase getPartUrlsUseCase;
    private final RecordUploadPartUseCase recordUploadPartUseCase;
    private final CompleteUploadUseCase completeUploadUseCase;
    private final CancelUploadUseCase cancelUploadUseCase;
    private final GetUploadStatusUseCase getUploadStatusUseCase;

    public AdminEpisodeController(
            CreateEpisodeUseCase createEpisodeUseCase,
            UpdateEpisodeUseCase updateEpisodeUseCase,
            ListEpisodesByMovieUseCase listEpisodesByMovieUseCase,
            DeleteEpisodeUseCase deleteEpisodeUseCase,
            UploadEpisodeThumbnailUseCase uploadEpisodeThumbnailUseCase,
            StartUploadUseCase startUploadUseCase,
            GetPartUrlsUseCase getPartUrlsUseCase,
            RecordUploadPartUseCase recordUploadPartUseCase,
            CompleteUploadUseCase completeUploadUseCase,
            CancelUploadUseCase cancelUploadUseCase,
            GetUploadStatusUseCase getUploadStatusUseCase
    ) {
        this.createEpisodeUseCase = createEpisodeUseCase;
        this.updateEpisodeUseCase = updateEpisodeUseCase;
        this.listEpisodesByMovieUseCase = listEpisodesByMovieUseCase;
        this.deleteEpisodeUseCase = deleteEpisodeUseCase;
        this.uploadEpisodeThumbnailUseCase = uploadEpisodeThumbnailUseCase;
        this.startUploadUseCase = startUploadUseCase;
        this.getPartUrlsUseCase = getPartUrlsUseCase;
        this.recordUploadPartUseCase = recordUploadPartUseCase;
        this.completeUploadUseCase = completeUploadUseCase;
        this.cancelUploadUseCase = cancelUploadUseCase;
        this.getUploadStatusUseCase = getUploadStatusUseCase;
    }

    // ===== CRUD =====

    @Operation(summary = "Tạo mới tập phim cho một bộ phim. Quyền truy cập: ADMIN.")
    @PostMapping
    public ResponseEntity<EpisodeResult> create(
            @PathVariable String movieId,
            @Valid @RequestBody CreateEpisodeRequest request
    ) {
        EpisodeResult result = createEpisodeUseCase.execute(new CreateEpisodeCommand(
                movieId, request.episodeNumber(), request.title(),
                request.description(), request.duration(), request.airDate()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Cập nhật thông tin tập phim theo ID. Quyền truy cập: ADMIN.")
    @PatchMapping("/{id}")
    public ResponseEntity<EpisodeResult> update(
            @PathVariable String movieId,
            @PathVariable String id,
            @Valid @RequestBody UpdateEpisodeRequest request
    ) {
        EpisodeResult result = updateEpisodeUseCase.execute(new UpdateEpisodeCommand(
                id, movieId, request.episodeNumber(), request.title(),
                request.description(), request.duration(), request.airDate()));
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Lấy danh sách tất cả các tập của một bộ phim. Quyền truy cập: ADMIN.")
    @GetMapping
    public List<EpisodeResult> list(@PathVariable String movieId) {
        return listEpisodesByMovieUseCase.execute(movieId);
    }

    @Operation(summary = "Xóa mềm tập phim theo ID. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String movieId, @PathVariable String id) {
        deleteEpisodeUseCase.execute(movieId, id);
        return ResponseEntity.ok().build();
    }

    // ===== Thumbnail =====

    @Operation(summary = "Upload ảnh Thumbnail cho tập phim. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/thumbnail")
    public ResponseEntity<EpisodeResult> uploadThumbnail(
            @PathVariable String movieId,
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        EpisodeResult result = uploadEpisodeThumbnailUseCase.execute(
                movieId, id, file.getOriginalFilename(),
                file.getInputStream(), file.getSize(), file.getContentType());
        return ResponseEntity.ok(result);
    }

    // ===== Video Upload (reuse upload services with targetType=episode) =====

    @Operation(summary = "Khởi tạo phiên upload video cho tập phim. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/uploads")
    public ResponseEntity<UploadSessionResult> startUpload(
            @PathVariable String id,
            @Valid @RequestBody StartUploadRequest request
    ) {
        UploadSessionResult result = startUploadUseCase.execute(
                new StartUploadCommand(id, "episode", request.fileName(), request.fileSizeBytes()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Lấy trạng thái tiến độ phiên upload video tập phim. Quyền truy cập: ADMIN.")
    @GetMapping("/{id}/uploads/{uploadId}/status")
    public ResponseEntity<UploadSessionResult> getUploadStatus(@PathVariable String uploadId) {
        return ResponseEntity.ok(getUploadStatusUseCase.execute(uploadId));
    }

    @Operation(summary = "Lấy danh sách Presigned PUT URLs để upload từng chunk video tập phim. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/uploads/{uploadId}/part-urls")
    public ResponseEntity<List<PresignedUrlResult>> getPartUrls(
            @PathVariable String uploadId,
            @Valid @RequestBody GetPartUrlsRequest request
    ) {
        return ResponseEntity.ok(getPartUrlsUseCase.execute(uploadId, request.partNumbers()));
    }

    @Operation(summary = "Ghi nhận 1 part chunk video tập phim đã upload xong. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/uploads/{uploadId}/parts/{partNumber}/completed")
    public ResponseEntity<Void> recordPart(
            @PathVariable String uploadId,
            @PathVariable int partNumber,
            @Valid @RequestBody RecordPartRequest request
    ) {
        recordUploadPartUseCase.execute(
                new RecordUploadPartCommand(uploadId, partNumber, request.etag(), request.sizeBytes()));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xác nhận hoàn tất phiên upload và ghép file video tập phim trên MinIO. Quyền truy cập: ADMIN.")
    @PostMapping("/{id}/uploads/{uploadId}/complete")
    public ResponseEntity<UploadSessionResult> completeUpload(@PathVariable String uploadId) {
        return ResponseEntity.ok(completeUploadUseCase.execute(uploadId));
    }

    @Operation(summary = "Hủy bỏ phiên upload video tập phim. Quyền truy cập: ADMIN.")
    @DeleteMapping("/{id}/uploads/{uploadId}")
    public ResponseEntity<Void> cancelUpload(@PathVariable String uploadId) {
        cancelUploadUseCase.execute(uploadId);
        return ResponseEntity.ok().build();
    }
}
