// Enum định nghĩa trạng thái xử lý/tải lên của tệp video.
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
