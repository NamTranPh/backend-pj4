package com.example.backend_pj4.presentation.upload;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.backend_pj4.common.annotation.AuthResultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.dto.upload.UploadResult;
import com.example.backend_pj4.application.port.in.upload.UploadFileUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.presentation.upload.request.UploadRequest;
import com.example.backend_pj4.presentation.upload.response.UploadResponse;

import com.example.backend_pj4.infrastructure.security.AppUserDetails;

import com.example.backend_pj4.common.annotation.AuthRequired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Uploads")
@AuthRequired
@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {

    private final UploadFileUseCase uploadFileUseCase;

    public UploadController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @Operation(summary = "Upload file media (Avatar/Poster/Video). Quyền truy cập: Người dùng đã đăng nhập (Upload POSTER/VIDEO yêu cầu ADMIN).")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(
            @Valid @ModelAttribute UploadRequest request,
            @AuthResultUser UserDetails userDetails) throws IOException {

        if (request.file() == null || request.file().isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // id và role đều lấy từ principal — filter đã nạp sẵn, không cần tra lại database
        String userId = AppUserDetails.userIdOf(userDetails);
        UserRole userRole = AppUserDetails.roleOf(userDetails);

        UploadResult result = uploadFileUseCase.execute(new UploadFileCommand(
                request.file().getInputStream(),
                request.file().getSize(),
                request.file().getContentType(),
                request.file().getOriginalFilename(),
                request.type(),
                userRole,
                userId
        ));

        return ResponseEntity.ok(new UploadResponse(result.previewUrl(), result.objectKey()));
    }
}
