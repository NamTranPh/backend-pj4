package com.example.backend_pj4.presentation.movie;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.movie.RecordUploadPartCommand;
import com.example.backend_pj4.application.command.movie.StartUploadCommand;
import com.example.backend_pj4.application.dto.movie.PresignedUrlResult;
import com.example.backend_pj4.application.dto.movie.UploadSessionResult;
import com.example.backend_pj4.application.port.in.movie.CancelUploadUseCase;
import com.example.backend_pj4.application.port.in.movie.CompleteUploadUseCase;
import com.example.backend_pj4.application.port.in.movie.GetPartUrlsUseCase;
import com.example.backend_pj4.application.port.in.movie.GetUploadStatusUseCase;
import com.example.backend_pj4.application.port.in.movie.RecordUploadPartUseCase;
import com.example.backend_pj4.application.port.in.movie.StartUploadUseCase;
import com.example.backend_pj4.presentation.movie.request.GetPartUrlsRequest;
import com.example.backend_pj4.presentation.movie.request.RecordPartRequest;
import com.example.backend_pj4.presentation.movie.request.StartUploadRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/admin/movies/{movieId}/uploads")
public class AdminMovieUploadController {

    private final StartUploadUseCase startUploadUseCase;
    private final GetPartUrlsUseCase getPartUrlsUseCase;
    private final RecordUploadPartUseCase recordUploadPartUseCase;
    private final CompleteUploadUseCase completeUploadUseCase;
    private final CancelUploadUseCase cancelUploadUseCase;
    private final GetUploadStatusUseCase getUploadStatusUseCase;

    public AdminMovieUploadController(
            StartUploadUseCase startUploadUseCase,
            GetPartUrlsUseCase getPartUrlsUseCase,
            RecordUploadPartUseCase recordUploadPartUseCase,
            CompleteUploadUseCase completeUploadUseCase,
            CancelUploadUseCase cancelUploadUseCase,
            GetUploadStatusUseCase getUploadStatusUseCase
    ) {
        this.startUploadUseCase = startUploadUseCase;
        this.getPartUrlsUseCase = getPartUrlsUseCase;
        this.recordUploadPartUseCase = recordUploadPartUseCase;
        this.completeUploadUseCase = completeUploadUseCase;
        this.cancelUploadUseCase = cancelUploadUseCase;
        this.getUploadStatusUseCase = getUploadStatusUseCase;
    }

    @PostMapping
    public ResponseEntity<UploadSessionResult> startUpload(
            @PathVariable String movieId,
            @Valid @RequestBody StartUploadRequest request
    ) {
        UploadSessionResult result = startUploadUseCase.execute(
                new StartUploadCommand(movieId, "movie", request.fileName(), request.fileSizeBytes()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{uploadId}/status")
    public ResponseEntity<UploadSessionResult> getStatus(@PathVariable String uploadId) {
        return ResponseEntity.ok(getUploadStatusUseCase.execute(uploadId));
    }

    @PostMapping("/{uploadId}/part-urls")
    public ResponseEntity<List<PresignedUrlResult>> getPartUrls(
            @PathVariable String uploadId,
            @Valid @RequestBody GetPartUrlsRequest request
    ) {
        return ResponseEntity.ok(getPartUrlsUseCase.execute(uploadId, request.partNumbers()));
    }

    @PostMapping("/{uploadId}/parts/{partNumber}/completed")
    public ResponseEntity<Void> recordPart(
            @PathVariable String uploadId,
            @PathVariable int partNumber,
            @Valid @RequestBody RecordPartRequest request
    ) {
        recordUploadPartUseCase.execute(
                new RecordUploadPartCommand(uploadId, partNumber, request.etag(), request.sizeBytes()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{uploadId}/complete")
    public ResponseEntity<UploadSessionResult> completeUpload(@PathVariable String uploadId) {
        return ResponseEntity.ok(completeUploadUseCase.execute(uploadId));
    }

    @DeleteMapping("/{uploadId}")
    public ResponseEntity<Void> cancelUpload(@PathVariable String uploadId) {
        cancelUploadUseCase.execute(uploadId);
        return ResponseEntity.ok().build();
    }
}
