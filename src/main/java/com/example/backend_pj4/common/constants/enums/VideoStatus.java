// Enum định nghĩa trạng thái xử lý/tải lên của tệp video (Bản nháp, Đang xử lý, Sẵn sàng, Đã xuất bản, Thất bại, Bị từ chối, Bị cấm).
package com.example.backend_pj4.common.constants.enums;

public enum VideoStatus {
    DRAFT,
    PROCESSING,
    READY,
    PUBLISHED,
    FAILED,
    REJECTED,
    BANNED
}
