package com.example.backend_pj4.application.usecase.upload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.backend_pj4.application.command.upload.UploadFileCommand;
import com.example.backend_pj4.application.port.out.FileStorageService;
import com.example.backend_pj4.common.constants.ErrorCode;
import com.example.backend_pj4.common.constants.enums.UploadType;
import com.example.backend_pj4.common.constants.enums.UserRole;
import com.example.backend_pj4.common.exceptions.CustomException;
import com.example.backend_pj4.domain.repository.UserRepository;
import com.example.backend_pj4.infrastructure.config.properties.MinioProperties;

/**
 * Khoá giới hạn dung lượng theo từng loại upload.
 *
 * <p>{@code spring.servlet.multipart.max-file-size} đặt chung 5000MB để phục vụ luồng upload
 * video, nên nếu không chặn theo loại ở tầng use case thì bất kỳ người dùng đã đăng nhập nào
 * cũng đẩy được một "avatar" 5GB lên object storage.
 */
class UploadSizeLimitTest {

    private static final long MB = 1024 * 1024;

    private FileStorageService fileStorageService;
    private UploadFileService service;

    @BeforeEach
    void setUp() {
        fileStorageService = mock(FileStorageService.class);
        UserRepository userRepository = mock(UserRepository.class);

        MinioProperties minio = new MinioProperties();
        minio.setBucketAvatar("avatars");
        minio.setBucketPublic("movie-public");
        minio.setBucketRaw("movie-raw");

        lenient().when(fileStorageService.upload(anyString(), anyString(), anyString(), any(), anyLong(), anyString()))
                .thenReturn("key.jpg");
        lenient().when(fileStorageService.getPublicUrl(anyString(), anyString())).thenReturn("http://x/key.jpg");
        lenient().when(fileStorageService.getPresignedGetUrl(anyString(), anyString(), anyInt()))
                .thenReturn("http://x/key.jpg?sig");

        service = new UploadFileService(fileStorageService, minio, userRepository);
    }

    private UploadFileCommand cmd(UploadType type, long size, String contentType, UserRole role) {
        return new UploadFileCommand(
                new ByteArrayInputStream(new byte[0]), size, contentType, "f.jpg", type, role, null);
    }

    private ErrorCode errorOf(Runnable action) {
        try {
            action.run();
            throw new AssertionError("mong đợi CustomException nhưng không có lỗi nào");
        } catch (CustomException e) {
            return e.getErrorCode();
        }
    }

    @Test
    void avatarDuoiNguong_choQua() {
        service.execute(cmd(UploadType.AVATAR, 4 * MB, "image/jpeg", UserRole.USER));

        verify(fileStorageService).upload(anyString(), anyString(), anyString(), any(), anyLong(), anyString());
    }

    @Test
    void avatar5GB_biTuChoi_khongDungToiObjectStorage() {
        assertThat(errorOf(() -> service.execute(cmd(UploadType.AVATAR, 5000 * MB, "image/jpeg", UserRole.USER))))
                .isEqualTo(ErrorCode.FILE_TOO_LARGE);

        verify(fileStorageService, never())
                .upload(anyString(), anyString(), anyString(), any(), anyLong(), anyString());
    }

    @Test
    void posterVuotNguong_biTuChoi() {
        assertThat(errorOf(() -> service.execute(cmd(UploadType.POSTER, 50 * MB, "image/png", UserRole.ADMIN))))
                .isEqualTo(ErrorCode.FILE_TOO_LARGE);
    }

    @Test
    void videoVanDuocPhepLon() {
        service.execute(cmd(UploadType.VIDEO, 2000 * MB, "video/mp4", UserRole.ADMIN));

        verify(fileStorageService).upload(anyString(), anyString(), anyString(), any(), anyLong(), anyString());
    }

    @Test
    void chanDungLuongChayTRUOCKhiChamObjectStorage() {
        errorOf(() -> service.execute(cmd(UploadType.AVATAR, 100 * MB, "image/jpeg", UserRole.USER)));

        // Nếu kiểm sau khi upload thì file rác đã nằm trên storage rồi mới báo lỗi
        verify(fileStorageService, never())
                .upload(anyString(), anyString(), anyString(), any(), anyLong(), anyString());
    }
}
