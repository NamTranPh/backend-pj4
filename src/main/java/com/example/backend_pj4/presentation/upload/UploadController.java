package com.example.backend_pj4.presentation.upload;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.dto.upload.UploadResult;
import com.example.backend_pj4.application.dto.user.UserProfileResult;
import com.example.backend_pj4.application.port.in.auth.GetCurrentUserUseCase;
import com.example.backend_pj4.application.port.in.upload.UploadFileUseCase;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UploadType;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.presentation.upload.request.UploadRequest;
import com.example.backend_pj4.presentation.upload.response.UploadResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/uploads")
public class UploadController {

    private final UploadFileUseCase uploadFileUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public UploadController(UploadFileUseCase uploadFileUseCase,
                            GetCurrentUserUseCase getCurrentUserUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(
            @Valid @ModelAttribute UploadRequest request,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        if (request.file() == null || request.file().isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // Lấy thông tin user hiện tại từ DB/Cache
        UserProfileResult currentUser = getCurrentUserUseCase.execute(userDetails.getUsername());
        UserRole userRole = UserRole.valueOf(currentUser.role()); // Ánh xạ Enum UserRole cực kỳ sạch sẽ

        UploadResult result = uploadFileUseCase.execute(new UploadFileCommand(
                request.file().getInputStream(),
                request.file().getSize(),
                request.file().getContentType(),
                request.file().getOriginalFilename(),
                request.type(),
                userRole
        ));

        return ResponseEntity.ok(new UploadResponse(result.fileUrl(), result.objectKey()));
    }
}
