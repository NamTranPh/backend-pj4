package com.example.backend_pj4.common.constants;

public class UploadConstants {
    public static final long PART_SIZE_BYTES = 16L * 1024 * 1024;           // 16MB per part
    public static final long MAX_UPLOAD_SIZE_BYTES = 50L * 1024 * 1024 * 1024;  // 50GB max
    public static final int UPLOAD_SESSION_TTL_HOURS = 24;                   // 24 hours
    public static final int PRESIGNED_URL_EXPIRY_SECONDS = 900;              // 15 minutes
    public static final int MAX_PART_NUMBER = 10_000;
}
