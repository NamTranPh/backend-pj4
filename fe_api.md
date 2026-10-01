# FE_API.md - API Contract

> **Mục đích:** Tài liệu API cho Frontend - danh sách endpoint, request/response, flow

**Cập nhật lần cuối:** 2026-08-26  
**Version:** 4.1

---

## QUY ĐỊNH

- Hiển thị các api theo module
- Hiển thị list các enum trước tiên
- Hiển thị các luồng khi chạy các api theo chức năng (ví dụ: forgot password → send-otp → verify-otp → reset-password)
- Table format: STT, Chức năng, Method, Path, Auth, Request DTO, Response DTO
- Phân biệt rõ: User API, Admin API
- Đầy đủ DTO request, response, error message
- **Cập nhật mới lên trên (thứ tự mới nhất → cũ nhất)**

---

## Enums

### UserRole

```json
["ADMIN", "USER"]
```

### AccountStatus

```json
["ACTIVE", "INACTIVE"]
```

### GenreStatus

```json
["ACTIVE", "INACTIVE"]
```

### MovieType

```json
["SINGLE", "SERIES"]
```

### VideoStatus

```json
["DRAFT", "PROCESSING", "READY", "PUBLISHED", "FAILED", "REJECTED", "BANNED"]
```

### VideoVisibility

```json
["PUBLIC", "PRIVATE", "MEMBERS_ONLY"]
```

### UploadType

```json
["AVATAR", "POSTER", "VIDEO"]
```

### OtpType

```json
["REGISTRATION", "PASSWORD_RESET"]
```

### MembershipPaymentStatus

```json
["PENDING", "PAID", "FAILED", "REFUNDED"]
```

### PaymentOrderStatus

```json
["PENDING", "COMPLETED", "FAILED", "CANCELLED"]
```

### PaymentGateway

```json
["VNPAY", "MOMO", "PAYOS"]
```

### PaymentStatus

```json
["PENDING", "COMPLETED", "FAILED", "REFUNDED"]
```

### SortOrder

```json
["asc", "desc"]
```

### NotificationType

```json
[
  "NEW_REPORT",
  "REPORT_RESOLVED",
  "NEW_EPISODE",
  "MEMBERSHIP_EXPIRY",
  "COMMENT_REPLY",
  "SYSTEM"
]
```

### NotificationReferenceType

```json
["REPORT", "MOVIE", "EPISODE", "COMMENT", "MEMBERSHIP"]
```

### ReportTargetType

```json
["COMMENT", "MOVIE", "USER"]
```

### ReportReason

```json
["SPAM", "INAPPROPRIATE", "COPYRIGHT", "BROKEN_CONTENT", "OTHER"]
```

### ReportStatus

```json
["PENDING", "REVIEWING", "RESOLVED", "REJECTED"]
```

---

## API Flows

### 1. Registration Flow

```
POST /api/v1/auth/register
  → Request: email, password, name
  → Response: { userId, email, otpSent }

POST /api/v1/auth/resend-registration-otp
  → Request: email
  → (Nếu chưa verify)

POST /api/v1/auth/verify-registration
  → Request: email, otpCode
  → Response: accessToken + cookies
```

### 2. Login Flow

```
POST /api/v1/auth/login
  → Request: email, password
  → Response: accessToken (in cookies)
```

### 3. Forgot Password Flow

```
POST /api/v1/auth/forgot-password
  → Request: email
  → Response: OTP sent to email

POST /api/v1/auth/reset-password
  → Request: email, otpCode, newPassword
  → Response: success, cookies cleared
```

### 4. Token Refresh

```
POST /api/v1/auth/refresh-token
  → Cookie: refresh_token        (admin: admin_refresh_token)
  → KHÔNG cần header Authorization — endpoint này gọi được khi access token đã hết hạn
  → Response: new accessToken + refresh token cookie mới (rotation)
```

Tương tự cho CMS: `POST /api/v1/admin/auth/refresh-token`.

### 5. Video Upload Flow (Movie hoặc Episode)

```
POST /api/v1/admin/movies/{movieId}/uploads
  → Request: fileName, fileSizeBytes
  → Response: uploadId, rawFileKey, partSizeBytes (16MB), expiresAt (24h)

POST /api/v1/admin/movies/{movieId}/uploads/{uploadId}/part-urls
  → Request: partNumbers [1,2,3...]
  → Response: presigned URLs (900s expiry mỗi URL)

[Client upload parts trực tiếp lên MinIO qua presigned URL]

POST /api/v1/admin/movies/{movieId}/uploads/{uploadId}/parts/{partNumber}/completed
  → Request: etag (từ MinIO response), sizeBytes
  → Response: recorded

POST /api/v1/admin/movies/{movieId}/uploads/{uploadId}/complete
  → Response: upload hoàn tất

GET /api/v1/admin/movies/{movieId}/uploads/{uploadId}/status
  → Response: parts[], progress, expiresAt (dùng để resume)

DELETE /api/v1/admin/movies/{movieId}/uploads/{uploadId}
  → Hủy upload, cleanup MinIO
```

Episode upload tương tự, path: `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/...`

### 6. Payment Flow (PayOS)

```
GET /api/v1/membership-plans
  → Response: List<MembershipPlanResult> (active plans)

POST /api/v1/payments/create-order
  → Request: { planSlug }
  → Response: { id, planName, price, status: "PENDING", checkoutUrl, transactionCode }

[Frontend redirect user tới checkoutUrl → User thanh toán trên PayOS]

[PayOS gọi webhook]
POST /api/v1/payments/payos/webhook
  → Body: raw JSON từ PayOS (verify HMAC-SHA256)
  → Backend: cập nhật order → kích hoạt/gia hạn Membership → tạo Notification

[Frontend poll trạng thái]
GET /api/v1/payments/{transactionCode}/status
  → Response: { id, planName, price, status: "COMPLETED", transactionCode }
```

### 7. Streaming Flow (HLS)

```
POST /api/v1/movies/{movieSlug}/play
  → Auth: User (membership check cho premium content)
  → Response: { contentId, contentType: "MOVIE", playbackUrl, playbackToken, resumePositionSeconds }

[Frontend dùng playbackUrl + token để stream]
GET /api/v1/stream/{contentId}/master.m3u8?token={playbackToken}
  → Response: HLS master playlist (application/vnd.apple.mpegurl)
  → Các URL trong playlist tự rewrite thành /api/v1/stream/{contentId}/segments/...?token=...

GET /api/v1/stream/{contentId}/segments/{path}?token={playbackToken}
  → Response: HLS variant playlist hoặc .ts segment
```

Episode streaming tương tự: `POST /api/v1/movies/{movieSlug}/episodes/{episodeNumber}/play`

---

## AUTH Module (Public)

| STT | Chức năng          | Method | Path                                   | Auth   | Request                     | Response                |
| --- | ------------------ | ------ | -------------------------------------- | ------ | --------------------------- | ----------------------- |
| 1   | Đăng ký            | POST   | `/api/v1/auth/register`                | Public | email, password, name       | RegisterResponse        |
| 2   | Verify OTP đăng ký | POST   | `/api/v1/auth/verify-registration`     | Public | email, otpCode              | void (no body)          |
| 3   | Resend OTP         | POST   | `/api/v1/auth/resend-registration-otp` | Public | email                       | message                 |
| 4   | Login              | POST   | `/api/v1/auth/login`                   | Public | email, password             | LoginResponse + cookies |
| 5   | Refresh Token      | POST   | `/api/v1/auth/refresh-token`           | Cookie | -                           | LoginResponse + cookies |
| 6   | Forgot Password    | POST   | `/api/v1/auth/forgot-password`         | Public | email                       | message                 |
| 7   | Reset Password     | POST   | `/api/v1/auth/reset-password`          | Public | email, otpCode, newPassword | message                 |
| 8   | Change Password    | POST   | `/api/v1/auth/change-password`         | User   | oldPassword, newPassword    | message                 |
| 9   | Logout             | POST   | `/api/v1/auth/logout`                  | Cookie | -                           | message + clear cookies |

**Request/Response DTO:**

```json
{
  "register": {
    "request": {
      "email": "string (required, email format, max 255)",
      "password": "string (required, min 6, max 72)",
      "name": "string (required, max 100)"
    },
    "response": {
      "userId": "string",
      "email": "string",
      "otpSent": true
    }
  },
  "login": {
    "request": {
      "email": "string (required, email format)",
      "password": "string (required)"
    },
    "response": {
      "accessToken": "string",
      "expiresIn": 3600
    }
  },
  "verify_registration": {
    "request": {
      "email": "string (required, email format)",
      "otpCode": "string (required)"
    },
    "response": "void (no body, HTTP 200)"
  },
  "reset_password": {
    "request": {
      "email": "string (required, email format)",
      "otpCode": "string (required)",
      "newPassword": "string (required, min 6, max 72)"
    }
  },
  "change_password": {
    "request": {
      "oldPassword": "string (required)",
      "newPassword": "string (required, min 6, max 72)"
    }
  }
}
```

---

## ADMIN AUTH Module

| STT | Chức năng           | Method | Path                               | Auth   | Request         | Response                |
| --- | ------------------- | ------ | ---------------------------------- | ------ | --------------- | ----------------------- |
| 1   | Admin Login         | POST   | `/api/v1/admin/auth/login`         | Public | email, password | LoginResponse + cookies |
| 2   | Admin Refresh Token | POST   | `/api/v1/admin/auth/refresh-token` | Cookie | -               | LoginResponse + cookies |
| 3   | Admin Logout        | POST   | `/api/v1/admin/auth/logout`        | Cookie | -               | message + clear cookies |
| 4   | Admin Get Me        | GET    | `/api/v1/admin/auth/me`            | Admin  | -               | UserProfileResult       |

---

## USER Module (Admin)

> Profile cá nhân (get/update/avatar) nằm ở **ACCOUNT Module** (`/api/v1/account/me`).

| STT | Chức năng      | Method | Path                           | Auth  | Request                    | Response                  | Note       |
| --- | -------------- | ------ | ------------------------------ | ----- | -------------------------- | ------------------------- | ---------- |
| 1   | Get all users  | GET    | `/api/v1/admin/users`          | Admin | -                          | List\<UserProfileResult\> | Admin only |
| 2   | Get user by ID | GET    | `/api/v1/admin/users/{id}`     | Admin | -                          | UserProfileResult         | Admin only |
| 3   | Toggle ban     | PATCH  | `/api/v1/admin/users/{id}/ban` | Admin | banned (boolean, required) | void                      | Admin only |

**UserProfileResult DTO:**

```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "phone": "string",
  "address": "string",
  "profileUrl": "url",
  "role": "ADMIN|USER",
  "accountStatus": "ACTIVE|INACTIVE",
  "emailVerified": true,
  "createdAt": "datetime"
}
```

**ToggleBanRequest:**

```json
{
  "banned": true
}
```

---

## UPLOAD Module (Generic)

| STT | Chức năng   | Method | Path              | Auth | Request                | Response                  | Note      |
| --- | ----------- | ------ | ----------------- | ---- | ---------------------- | ------------------------- | --------- |
| 1   | Upload file | POST   | `/api/v1/uploads` | User | file (multipart), type | { previewUrl, objectKey } | User only |

**Request:** `multipart/form-data` — field `file` (MultipartFile) + `type` (UploadType enum: AVATAR, POSTER, VIDEO)

---

## GENRE Module (Public & Admin)

### Public

| STT | Chức năng      | Method | Path                                     | Auth   | Request             | Response               |
| --- | -------------- | ------ | ---------------------------------------- | ------ | ------------------- | ---------------------- |
| 1   | Get all genres | GET    | `/api/v1/genres?search=&page=1&limit=20` | Public | search, page, limit | GenreList + pagination |

> Public chỉ trả genre có `status=ACTIVE`

### Admin

| STT | Chức năng        | Method | Path                                           | Auth  | Request                             | Response               |
| --- | ---------------- | ------ | ---------------------------------------------- | ----- | ----------------------------------- | ---------------------- |
| 1   | Create genre     | POST   | `/api/v1/admin/genres`                         | Admin | name, icon?, description?           | GenreResult            |
| 2   | Update genre     | PATCH  | `/api/v1/admin/genres/{id}`                    | Admin | name?, icon?, description?, status? | GenreResult            |
| 3   | Get genre detail | GET    | `/api/v1/admin/genres/{slug}`                  | Admin | -                                   | GenreResult            |
| 4   | Get all genres   | GET    | `/api/v1/admin/genres?search=&page=1&limit=10` | Admin | search, page, limit                 | GenreList + pagination |
| 5   | Delete genre     | DELETE | `/api/v1/admin/genres/{id}`                    | Admin | -                                   | void                   |

> Admin GET detail dùng slug (hỗ trợ fallback UUID). Admin list trả tất cả status.

**GenreResult DTO:**

```json
{
  "id": "uuid",
  "name": "string",
  "slug": "string",
  "icon": "string",
  "description": "string",
  "status": "ACTIVE|INACTIVE",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Request DTOs:**

```json
{
  "CreateGenreRequest": {
    "name": "string (required, max 100)",
    "icon": "string (max 255)",
    "description": "string (max 500)"
  },
  "UpdateGenreRequest": {
    "name": "string (max 100)",
    "icon": "string (max 255)",
    "description": "string (max 500)",
    "status": "ACTIVE|INACTIVE"
  }
}
```

---

## MOVIE Module (Public & Admin)

### Public

| STT | Chức năng        | Method | Path                                                                   | Auth   | Request                                           | Response               |
| --- | ---------------- | ------ | ---------------------------------------------------------------------- | ------ | ------------------------------------------------- | ---------------------- |
| 1   | Get all movies   | GET    | `/api/v1/movies?search=&type=&genreId=&year=&country=&page=1&limit=20` | Public | search, type, genreId, year, country, page, limit | MovieList + pagination |
| 2   | Get movie detail | GET    | `/api/v1/movies/{slug}`                                                | Public | -                                                 | MovieDetailResult      |

> Public chỉ trả movie không bị soft-delete. Detail kèm episodes (nếu SERIES).

### Admin

| STT | Chức năng        | Method | Path                                                                                 | Auth  | Request                                                                                                                                                                  | Response               |
| --- | ---------------- | ------ | ------------------------------------------------------------------------------------ | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------- |
| 1   | Create movie     | POST   | `/api/v1/admin/movies`                                                               | Admin | title, originalTitle?, description?, releaseYear?, duration?, director?, actors?, country?, language?, trailerUrl?, movieType (SINGLE/SERIES), totalEpisodes?, genreIds? | MovieResult            |
| 2   | Update movie     | PATCH  | `/api/v1/admin/movies/{id}`                                                          | Admin | (all fields optional + isFeatured?, isPremium?, status?, visibility?)                                                                                                    | MovieResult            |
| 3   | Get movie detail | GET    | `/api/v1/admin/movies/{slug}`                                                        | Admin | -                                                                                                                                                                        | MovieDetailResult      |
| 4   | Get all movies   | GET    | `/api/v1/admin/movies?search=&type=&status=&genreId=&year=&country=&page=1&limit=10` | Admin | search, type, status, genreId, year, country, page, limit                                                                                                                | MovieList + pagination |
| 5   | Delete movie     | DELETE | `/api/v1/admin/movies/{id}`                                                          | Admin | -                                                                                                                                                                        | void                   |
| 6   | Restore movie    | PATCH  | `/api/v1/admin/movies/{id}/restore`                                                  | Admin | -                                                                                                                                                                        | MovieResult            |
| 7   | Upload poster    | POST   | `/api/v1/admin/movies/{id}/poster`                                                   | Admin | file (multipart)                                                                                                                                                         | MovieResult            |
| 8   | Upload backdrop  | POST   | `/api/v1/admin/movies/{id}/backdrop`                                                 | Admin | file (multipart)                                                                                                                                                         | MovieResult            |
| 9   | Publish movie    | POST   | `/api/v1/admin/movies/{id}/publish`                                                  | Admin | -                                                                                                                                                                        | MovieResult            |

> Admin GET detail dùng slug (hỗ trợ fallback UUID).

**MovieResult DTO:**

```json
{
  "id": "uuid",
  "slug": "string",
  "title": "string",
  "originalTitle": "string",
  "description": "string",
  "releaseYear": "number",
  "duration": "number (minutes)",
  "director": "string",
  "actors": "string",
  "country": "string",
  "language": "string",
  "trailerUrl": "url",
  "posterUrl": "url",
  "backdropUrl": "url",
  "movieType": "SINGLE|SERIES",
  "totalEpisodes": "number",
  "status": "DRAFT|PROCESSING|READY|PUBLISHED|FAILED|REJECTED|BANNED",
  "visibility": "PUBLIC|PRIVATE|MEMBERS_ONLY",
  "isPremium": "boolean",
  "isFeatured": "boolean",
  "rating": "decimal (0.0-10.0)",
  "viewCount": "number",
  "genres": ["GenreResult"],
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**MovieDetailResult DTO** (extends MovieResult):

```json
{
  "...MovieResult fields",
  "rawFileKey": "string",
  "masterPlaylistKey": "string",
  "episodes": ["EpisodeResult"],
  "createdByName": "string"
}
```

---

## MOVIE VIDEO UPLOAD Module (Admin)

| STT | Chức năng         | Method | Path                                                                             | Auth  | Request                 | Response             |
| --- | ----------------- | ------ | -------------------------------------------------------------------------------- | ----- | ----------------------- | -------------------- |
| 1   | Start upload      | POST   | `/api/v1/admin/movies/{movieId}/uploads`                                         | Admin | fileName, fileSizeBytes | UploadSessionResult  |
| 2   | Get upload status | GET    | `/api/v1/admin/movies/{movieId}/uploads/{uploadId}/status`                       | Admin | -                       | UploadSessionResult  |
| 3   | Get part URLs     | POST   | `/api/v1/admin/movies/{movieId}/uploads/{uploadId}/part-urls`                    | Admin | partNumbers [1,2,3...]  | [PresignedUrlResult] |
| 4   | Record part done  | POST   | `/api/v1/admin/movies/{movieId}/uploads/{uploadId}/parts/{partNumber}/completed` | Admin | etag, sizeBytes         | void                 |
| 5   | Complete upload   | POST   | `/api/v1/admin/movies/{movieId}/uploads/{uploadId}/complete`                     | Admin | -                       | UploadSessionResult  |
| 6   | Cancel upload     | DELETE | `/api/v1/admin/movies/{movieId}/uploads/{uploadId}`                              | Admin | -                       | void                 |

**UploadSessionResult DTO:**

```json
{
  "id": "uuid",
  "targetId": "uuid (movieId)",
  "uploadId": "string (MinIO upload ID)",
  "rawFileKey": "string",
  "bucket": "string",
  "fileSizeBytes": "number",
  "fileName": "string",
  "partSizeBytes": 16777216,
  "status": "ACTIVE|COMPLETED|ABORTED",
  "parts": [
    {
      "partNumber": 1,
      "etag": "string",
      "sizeBytes": "number",
      "uploadedAt": "datetime"
    }
  ],
  "expiresAt": "datetime",
  "createdAt": "datetime"
}
```

**PresignedUrlResult DTO:**

```json
{
  "partNumber": 1,
  "uploadUrl": "https://minio/...presigned-url..."
}
```

---

## EPISODE Module (Admin)

### CRUD

| STT | Chức năng        | Method | Path                                                     | Auth  | Request                                                   | Response        |
| --- | ---------------- | ------ | -------------------------------------------------------- | ----- | --------------------------------------------------------- | --------------- |
| 1   | Create episode   | POST   | `/api/v1/admin/movies/{movieId}/episodes`                | Admin | episodeNumber, title?, description?, duration?, airDate?  | EpisodeResult   |
| 2   | Update episode   | PATCH  | `/api/v1/admin/movies/{movieId}/episodes/{id}`           | Admin | episodeNumber?, title?, description?, duration?, airDate? | EpisodeResult   |
| 3   | List episodes    | GET    | `/api/v1/admin/movies/{movieId}/episodes`                | Admin | -                                                         | [EpisodeResult] |
| 4   | Delete episode   | DELETE | `/api/v1/admin/movies/{movieId}/episodes/{id}`           | Admin | -                                                         | void            |
| 5   | Upload thumbnail | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/thumbnail` | Admin | file (multipart)                                          | EpisodeResult   |
| 5.1 | Publish episode  | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/publish`   | Admin | -                                                         | EpisodeResult   |

### Episode Video Upload

| STT | Chức năng         | Method | Path                                                                                           | Auth  | Request                 | Response             |
| --- | ----------------- | ------ | ---------------------------------------------------------------------------------------------- | ----- | ----------------------- | -------------------- |
| 6   | Start upload      | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads`                                         | Admin | fileName, fileSizeBytes | UploadSessionResult  |
| 7   | Get upload status | GET    | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/{uploadId}/status`                       | Admin | -                       | UploadSessionResult  |
| 8   | Get part URLs     | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/{uploadId}/part-urls`                    | Admin | partNumbers [1,2,3...]  | [PresignedUrlResult] |
| 9   | Record part done  | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/{uploadId}/parts/{partNumber}/completed` | Admin | etag, sizeBytes         | void                 |
| 10  | Complete upload   | POST   | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/{uploadId}/complete`                     | Admin | -                       | UploadSessionResult  |
| 11  | Cancel upload     | DELETE | `/api/v1/admin/movies/{movieId}/episodes/{id}/uploads/{uploadId}`                              | Admin | -                       | void                 |

> Public: Episodes trả kèm trong MovieDetailResult khi GET `/api/v1/movies/{slug}` (nếu SERIES)

**EpisodeResult DTO:**

```json
{
  "id": "uuid",
  "movieId": "uuid",
  "episodeNumber": "number",
  "title": "string",
  "description": "string",
  "duration": "number (minutes)",
  "status": "DRAFT|PROCESSING|READY|PUBLISHED|FAILED|REJECTED|BANNED",
  "thumbnailUrl": "url",
  "rawFileKey": "string",
  "masterPlaylistKey": "string",
  "airDate": "date",
  "viewCount": "number",
  "createdAt": "datetime"
}
```

---

## ACCOUNT Module (User)

| STT | Chức năng        | Method | Path                        | Auth | Request              | Response          |
| --- | ---------------- | ------ | --------------------------- | ---- | -------------------- | ----------------- |
| 1   | Xem profile      | GET    | `/api/v1/account/me`        | User | -                    | UserProfileResult |
| 2   | Cập nhật profile | PATCH  | `/api/v1/account/me`        | User | UpdateProfileRequest | UserProfileResult |
| 3   | Xóa avatar       | DELETE | `/api/v1/account/me/avatar` | User | -                    | void              |

**Request/Response DTO:**

```json
{
  "updateProfile": {
    "request": {
      "name": "string (optional, max 100)",
      "phone": "string (optional, max 20)",
      "address": "string (optional, max 500)",
      "profileUrl": "string (optional, max 500)"
    }
  }
}
```

---

## FAVORITE Module (User)

| STT | Chức năng      | Method | Path                               | Auth | Request | Response                    |
| --- | -------------- | ------ | ---------------------------------- | ---- | ------- | --------------------------- |
| 1   | Thêm yêu thích | POST   | `/api/v1/me/favorites/{movieSlug}` | User | -       | void (201)                  |
| 2   | Bỏ yêu thích   | DELETE | `/api/v1/me/favorites/{movieSlug}` | User | -       | void                        |
| 3   | Danh sách      | GET    | `/api/v1/me/favorites`             | User | -       | List\<FavoriteMovieResult\> |

**Response DTO:**

```json
{
  "favoriteMovie": {
    "movieId": "string",
    "movieSlug": "string",
    "movieTitle": "string",
    "posterUrl": "string (presigned URL)",
    "isPremium": true,
    "addedAt": "datetime"
  }
}
```

---

## HISTORY Module (User)

| STT | Chức năng            | Method | Path                                         | Auth | Request                    | Response                   |
| --- | -------------------- | ------ | -------------------------------------------- | ---- | -------------------------- | -------------------------- |
| 1   | Cập nhật tiến độ xem | POST   | `/api/v1/me/watch-history/progress`          | User | UpdateWatchProgressRequest | void                       |
| 2   | Xem tiếp (continue)  | GET    | `/api/v1/me/watch-history/continue-watching` | User | -                          | List\<WatchHistoryResult\> |
| 3   | Toàn bộ lịch sử      | GET    | `/api/v1/me/watch-history`                   | User | -                          | List\<WatchHistoryResult\> |

**Request/Response DTO:**

```json
{
  "updateWatchProgress": {
    "request": {
      "movieId": "string (required)",
      "episodeId": "string (required)",
      "positionSeconds": "int (required, min 0)",
      "durationSeconds": "int (required, min 1)"
    }
  },
  "watchHistory": {
    "response": {
      "id": "string",
      "movieId": "string",
      "movieSlug": "string",
      "movieTitle": "string",
      "posterUrl": "string (presigned URL)",
      "episodeId": "string",
      "episodeNumber": 1,
      "episodeTitle": "string",
      "lastPosition": 120,
      "totalDuration": 3600,
      "progress": 3.33,
      "isCompleted": false,
      "watchedAt": "datetime"
    }
  }
}
```

---

## MEMBERSHIP PLAN Module (Public & Admin)

### Public

| STT | Chức năng     | Method | Path                       | Auth   | Request | Response                     |
| --- | ------------- | ------ | -------------------------- | ------ | ------- | ---------------------------- |
| 1   | Danh sách gói | GET    | `/api/v1/membership-plans` | Public | -       | List\<MembershipPlanResult\> |

### Admin

| STT | Chức năng | Method | Path                                  | Auth  | Request                     | Response                     |
| --- | --------- | ------ | ------------------------------------- | ----- | --------------------------- | ---------------------------- |
| 1   | Tạo gói   | POST   | `/api/v1/admin/membership-plans`      | Admin | CreateMembershipPlanRequest | MembershipPlanResult (201)   |
| 2   | Sửa gói   | PATCH  | `/api/v1/admin/membership-plans/{id}` | Admin | UpdateMembershipPlanRequest | MembershipPlanResult         |
| 3   | Chi tiết  | GET    | `/api/v1/admin/membership-plans/{id}` | Admin | -                           | MembershipPlanResult         |
| 4   | Danh sách | GET    | `/api/v1/admin/membership-plans`      | Admin | -                           | List\<MembershipPlanResult\> |
| 5   | Xóa gói   | DELETE | `/api/v1/admin/membership-plans/{id}` | Admin | -                           | void                         |

**Request/Response DTO:**

```json
{
  "createMembershipPlan": {
    "request": {
      "name": "string (required, max 100)",
      "price": "decimal (required, positive)",
      "durationDays": "int (required, positive)",
      "maxDevices": "int (optional, positive)",
      "canDownload": "boolean (optional)",
      "videoQuality": "string (optional, max 20)",
      "description": "string (optional)"
    }
  },
  "updateMembershipPlan": {
    "request": {
      "name": "string (optional, max 100)",
      "price": "decimal (optional, positive)",
      "durationDays": "int (optional, positive)",
      "maxDevices": "int (optional, positive)",
      "canDownload": "boolean (optional)",
      "videoQuality": "string (optional, max 20)",
      "description": "string (optional)",
      "isActive": "boolean (optional)"
    }
  },
  "membershipPlan": {
    "response": {
      "id": "string",
      "name": "string",
      "slug": "string",
      "price": 99000,
      "durationDays": 30,
      "maxDevices": 2,
      "canDownload": true,
      "videoQuality": "1080p",
      "description": "string",
      "isActive": true,
      "createdAt": "datetime",
      "updatedAt": "datetime"
    }
  }
}
```

---

## PAYMENT Module (User)

| STT | Chức năng           | Method | Path                                        | Auth   | Request              | Response                 |
| --- | ------------------- | ------ | ------------------------------------------- | ------ | -------------------- | ------------------------ |
| 1   | Tạo đơn thanh toán  | POST   | `/api/v1/payments/create-order`             | User   | CreatePaymentRequest | PaymentOrderResult (201) |
| 2   | PayOS webhook       | POST   | `/api/v1/payments/payos/webhook`            | Public | raw JSON (PayOS)     | void                     |
| 3   | Kiểm tra trạng thái | GET    | `/api/v1/payments/{transactionCode}/status` | User   | -                    | PaymentOrderResult       |

**Request/Response DTO:**

```json
{
  "createPayment": {
    "request": {
      "planSlug": "string (required)"
    }
  },
  "paymentOrder": {
    "response": {
      "id": "string",
      "planName": "string",
      "price": 99000,
      "status": "PENDING | COMPLETED | FAILED | CANCELLED",
      "transactionCode": "string",
      "checkoutUrl": "string (PayOS checkout link, chỉ có khi PENDING)",
      "createdAt": "datetime",
      "completedAt": "datetime (nullable)"
    }
  }
}
```

> **Flow:** Tạo order → FE redirect tới `checkoutUrl` → User thanh toán → PayOS gọi webhook → Backend kích hoạt membership + tạo notification → FE poll status.

---

## NOTIFICATION Module (User)

| STT | Chức năng       | Method | Path                                    | Auth | Request | Response                   |
| --- | --------------- | ------ | --------------------------------------- | ---- | ------- | -------------------------- |
| 1   | Danh sách       | GET    | `/api/v1/me/notifications`              | User | -       | List\<NotificationResult\> |
| 2   | Số chưa đọc     | GET    | `/api/v1/me/notifications/unread-count` | User | -       | { "unreadCount": 5 }       |
| 3   | Đánh dấu đã đọc | PUT    | `/api/v1/me/notifications/{id}/read`    | User | -       | void                       |

**Response DTO:**

```json
{
  "notification": {
    "response": {
      "id": "string",
      "type": "NotificationType enum",
      "title": "string",
      "content": "string",
      "referenceType": "NotificationReferenceType enum (nullable)",
      "referenceId": "string (nullable)",
      "isRead": false,
      "readAt": "datetime (nullable)",
      "createdAt": "datetime"
    }
  }
}
```

---

## STREAMING Module (Token-based)

> Response **không bọc** ApiResponseDto (đánh dấu `@IgnoreResponseWrapping`). Trả raw HLS content.

### Playback Request

| STT | Chức năng    | Method | Path                                                       | Auth | Response          |
| --- | ------------ | ------ | ---------------------------------------------------------- | ---- | ----------------- |
| 1   | Play movie   | POST   | `/api/v1/movies/{movieSlug}/play`                          | User | PlayContentResult |
| 2   | Play episode | POST   | `/api/v1/movies/{movieSlug}/episodes/{episodeNumber}/play` | User | PlayContentResult |

**Response DTO:**

```json
{
  "playContent": {
    "response": {
      "contentId": "string (movie/episode UUID)",
      "contentType": "MOVIE | EPISODE",
      "playbackUrl": "/api/v1/stream/{contentId}/master.m3u8",
      "playbackToken": "string (HMAC token, TTL 300s)",
      "resumePositionSeconds": 120
    }
  }
}
```

### HLS Stream Proxy

| STT | Chức năng       | Method | Path                                         | Auth       | Response                      |
| --- | --------------- | ------ | -------------------------------------------- | ---------- | ----------------------------- |
| 1   | Master playlist | GET    | `/api/v1/stream/{contentId}/master.m3u8`     | ?token=... | HLS playlist (text)           |
| 2   | Segment/variant | GET    | `/api/v1/stream/{contentId}/segments/{path}` | ?token=... | HLS variant playlist hoặc .ts |

> Frontend dùng HLS.js hoặc native player, truyền `playbackUrl + "?token=" + playbackToken` làm source URL.

---

## PUBLIC EPISODE Module

| STT | Chức năng     | Method | Path                                  | Auth   | Response                    |
| --- | ------------- | ------ | ------------------------------------- | ------ | --------------------------- |
| 1   | Danh sách tập | GET    | `/api/v1/movies/{movieSlug}/episodes` | Public | List\<PublicEpisodeResult\> |

**Response DTO:**

```json
{
  "publicEpisode": {
    "response": {
      "episodeNumber": 1,
      "title": "string",
      "description": "string",
      "duration": 45,
      "thumbnailUrl": "string (presigned URL)",
      "status": "VideoStatus enum",
      "airDate": "date"
    }
  }
}
```

---

## COMMENT Module (User & Admin) — chưa implement

| STT | Chức năng      | Method | Path                                            | Auth       | Request                                 | Response                 | Note                |
| --- | -------------- | ------ | ----------------------------------------------- | ---------- | --------------------------------------- | ------------------------ | ------------------- |
| 1   | Get comments   | GET    | `/api/v1/comments?movieId={id}&page=0&limit=20` | Public     | movieId, page, limit                    | CommentList + pagination | Public              |
| 2   | Create comment | POST   | `/api/v1/comments`                              | User       | movieId, episodeId?, content, parentId? | CommentDetail            | User only           |
| 3   | Update comment | PATCH  | `/api/v1/comments/{id}`                         | User       | content                                 | CommentDetail            | User only           |
| 4   | Delete comment | DELETE | `/api/v1/comments/{id}`                         | User/Admin | -                                       | message                  | User (own) or Admin |

---

## RATING Module (User) — chưa implement

| STT | Chức năng            | Method | Path                                           | Auth       | Request                       | Response                | Note                |
| --- | -------------------- | ------ | ---------------------------------------------- | ---------- | ----------------------------- | ----------------------- | ------------------- |
| 1   | Get ratings          | GET    | `/api/v1/ratings?movieId={id}&page=0&limit=20` | Public     | movieId, page, limit          | RatingList + pagination | Public              |
| 2   | Create/Update rating | POST   | `/api/v1/ratings`                              | User       | movieId, score (int), review? | RatingDetail            | User only           |
| 3   | Delete rating        | DELETE | `/api/v1/ratings/{id}`                         | User/Admin | -                             | message                 | User (own) or Admin |

---

## REPORT Module (User & Admin) — chưa implement

| STT | Chức năng       | Method | Path                                    | Auth  | Request                                    | Response                | Note       |
| --- | --------------- | ------ | --------------------------------------- | ----- | ------------------------------------------ | ----------------------- | ---------- |
| 1   | Create report   | POST   | `/api/v1/reports`                       | User  | targetType, targetId, reason, description? | ReportDetail            | User only  |
| 2   | Get my reports  | GET    | `/api/v1/reports/me?page=0&limit=20`    | User  | page, limit                                | ReportList + pagination | User only  |
| 3   | Get all reports | GET    | `/api/v1/admin/reports?page=0&limit=20` | Admin | page, limit, status?, targetType?          | ReportList + pagination | Admin only |
| 4   | Resolve report  | PATCH  | `/api/v1/admin/reports/{id}/resolve`    | Admin | status, adminNote?                         | ReportDetail            | Admin only |

---

## Response Format (Standard)

**Success Response:**

```json
{
  "status": "success",
  "code": "success",
  "message": "Successfully",
  "data": {
    /* actual data */
  },
  "pagination": null,
  "errors": null,
  "timestamp": "2026-08-18T10:30:00Z"
}
```

**Success Response (với pagination):**

```json
{
  "status": "success",
  "code": "success",
  "message": "Successfully",
  "data": [
    /* items */
  ],
  "pagination": {
    "page": 1,
    "limit": 10,
    "totalItems": 150,
    "totalPages": 15
  },
  "errors": null,
  "timestamp": "2026-08-18T10:30:00Z"
}
```

**Error Response:**

```json
{
  "status": "error",
  "code": "error_code_snake_case",
  "message": "Error message in English",
  "data": null,
  "pagination": null,
  "errors": null,
  "timestamp": "2026-08-18T10:30:00Z"
}
```

**Validation Error Response:**

```json
{
  "status": "error",
  "code": "validation_failed",
  "message": "Validation failed",
  "data": null,
  "pagination": null,
  "errors": {
    "email": "Email is required.",
    "password": "Password must be at least 6 characters."
  },
  "timestamp": "2026-08-18T10:30:00Z"
}
```

---

## Authentication

**Access token — chỉ qua header Authorization:**

```
Authorization: Bearer <accessToken>
```

> Backend **không** đọc access token từ cookie nữa. Cookie chỉ dùng cho refresh token.

**Refresh token — httpOnly cookie, tách theo kênh:**

| Kênh        | Tên cookie            | Path                 |
| ----------- | --------------------- | -------------------- |
| User        | `refresh_token`       | `/api/v1/auth`       |
| Admin (CMS) | `admin_refresh_token` | `/api/v1/admin/auth` |

### Claim trong JWT

| Claim     | Access token          | Refresh token         | Ý nghĩa                                                                           |
| --------- | --------------------- | --------------------- | --------------------------------------------------------------------------------- |
| `sub`     | email                 | email                 | Chủ sở hữu token                                                                  |
| `typ`     | `"access"`            | —                     | Phân biệt loại token. Refresh token gửi qua header `Authorization` sẽ bị từ chối. |
| `type`    | —                     | `"refresh"`           | Giữ tên cũ cho refresh token                                                      |
| `tokenId` | —                     | UUID                  | Khoá tra cứu trong refresh token store (để revoke)                                |
| `ch`      | `"admin"` \| `"user"` | `"admin"` \| `"user"` | **Kênh phát token**                                                               |

### Cô lập kênh (claim `ch`)

`ch` cho biết token được phát ở **cửa đăng nhập nào**, không phải role:

- Login `/api/v1/auth/login` → `ch="user"` → **không** gọi được `/api/v1/admin/**` (403), kể cả khi tài khoản đó có role ADMIN.
- Login `/api/v1/admin/auth/login` → `ch="admin"` → gọi được **cả hai kênh** (admin có thể dùng API phía user để test hệ thống).

| Tài khoản | Login ở                    | `ch`    | `/api/v1/admin/**`    | API user |
| --------- | -------------------------- | ------- | --------------------- | -------- |
| USER      | `/api/v1/auth/login`       | `user`  | ✗ 403                 | ✓        |
| USER      | `/api/v1/admin/auth/login` | —       | `admin_role_required` | —        |
| ADMIN     | `/api/v1/auth/login`       | `user`  | ✗ 403                 | ✓        |
| ADMIN     | `/api/v1/admin/auth/login` | `admin` | ✓                     | ✓        |

**Lưu ý FE:** hai frontend nên lưu access token ở key riêng biệt và không dùng chéo token của nhau.

---

## Cập nhật lịch sử

Ghi cập nhật mới dưới đây (thứ tự mới nhất → cũ nhất)

### 2026-09-21 - v4.2 - Auth: đổi mã lỗi để chống dò tài khoản

**Endpoint, HTTP method, request/response DTO: KHÔNG ĐỔI.** Chỉ mã lỗi thay đổi.

#### Vì sao đổi

Trước bản này, chỉ cần gửi mật khẩu bừa rồi đọc mã lỗi là dò ra được email nào đã đăng ký trong hệ thống, email nào bị ban, và **tài khoản nào là admin** — không cần biết mật khẩu. Nay mọi thất bại **trước khi mật khẩu đúng** đều trả cùng một mã `bad_credentials`.

#### Bảng thay đổi mã lỗi

| Endpoint                                    | Tình huống                                       | Trước                                                                             | Sau                                                               |
| ------------------------------------------- | ------------------------------------------------ | --------------------------------------------------------------------------------- | ----------------------------------------------------------------- |
| `POST /api/v1/auth/login`                   | Sai mật khẩu, tài khoản chưa xác thực email      | `email_not_verified` (403)                                                        | `bad_credentials` (401)                                           |
| `POST /api/v1/auth/login`                   | Sai mật khẩu, tài khoản bị ban                   | `account_banned` (403)                                                            | `bad_credentials` (401)                                           |
| `POST /api/v1/auth/login`                   | Sai mật khẩu, tài khoản không active             | `account_not_active` (403)                                                        | `bad_credentials` (401)                                           |
| `POST /api/v1/admin/auth/login`             | Sai mật khẩu, tài khoản không phải admin         | `admin_role_required` (403)                                                       | `bad_credentials` (401)                                           |
| `POST /api/v1/auth/resend-registration-otp` | Email chưa đăng ký / đã kích hoạt / còn cooldown | `user_not_found` (404), `email_already_exists` (409), `otp_resend_too_soon` (429) | **200 silent success** (không tiết lộ email có tồn tại hay không) |
| `POST /api/v1/auth/reset-password`          | Email chưa đăng ký                               | `user_not_found` (404)                                                            | `otp_invalid` (400)                                               |

#### Không đổi — luồng FE hiện tại vẫn chạy đúng

| Tình huống                                            | Mã lỗi                             |
| ----------------------------------------------------- | ---------------------------------- |
| **Đúng** mật khẩu, chưa xác thực email                | `email_not_verified` (403)         |
| **Đúng** mật khẩu, bị ban                             | `account_banned` (403)             |
| **Đúng** mật khẩu, không active                       | `account_not_active` (403)         |
| **Đúng** mật khẩu ở cửa admin, không phải admin       | `admin_role_required` (403)        |
| Tài khoản đang bị khoá tạm (dù mật khẩu đúng hay sai) | `account_temporarily_locked` (429) |

Luồng "login → nếu `email_not_verified` thì chuyển sang màn nhập OTP" **vẫn hoạt động** — chỉ khác là người dùng phải nhập đúng mật khẩu mới được chuyển, đúng như mong đợi về bảo mật.

#### Lưu ý cho FE

- `POST /api/v1/auth/resend-registration-otp` giờ **luôn trả 200**. FE nên hiển thị thông báo trung lập kiểu "Nếu email hợp lệ, mã OTP đã được gửi" thay vì khẳng định đã gửi thành công.
- `account_temporarily_locked` (429) vẫn được trả **trước** khi kiểm tra mật khẩu — đây là rate limit chống brute-force, không phải trạng thái tài khoản.

#### Thay đổi nội bộ không ảnh hưởng FE

- Mỗi phiên đăng nhập nay được gắn `session_id` cố định (giữ nguyên qua mọi lần refresh) cùng `user_agent`, `ip_address`, `last_used_at` — chuẩn bị cho tính năng **Quản lý phiên đăng nhập** (liệt kê thiết bị, đăng xuất từ xa). Endpoint cho tính năng này **chưa có**, sẽ bổ sung sau.
- Refresh token của kênh admin không còn dùng được ở `POST /api/v1/auth/refresh-token` và ngược lại — trả `refresh_token_revoked` (401).
- Refresh token hết hạn được dọn tự động hằng ngày lúc 02:30.

### 2026-09-07 - v4.1 - Auth Security Hardening

**⚠️ BREAKING (một phần):** access token phát trước bản này bị từ chối vì thiếu claim `typ`/`ch`. **Refresh token cũ vẫn hợp lệ** (backend lấy kênh từ DB, không đọc `ch` của token gửi lên).

|               | Token phát trước bản này | Sau khi deploy                                 |
| ------------- | ------------------------ | ---------------------------------------------- |
| Access token  | thiếu `typ`/`ch`         | **bị từ chối** — mỗi client nhận 1 lần 401/403 |
| Refresh token | có `type` + `tokenId`    | **vẫn dùng được**                              |

- **FE có interceptor auto-refresh** (bắt 401/403 → gọi `/refresh-token` → retry request gốc): người dùng **không bị đăng xuất**, chỉ chậm một nhịp.
- **FE chưa có interceptor:** người dùng phải login lại. Nên bổ sung interceptor — access token hết hạn mỗi 15 phút, không có nó thì user bị đá ra mỗi 15 phút dù không deploy gì.

- **Claim mới trong JWT:** `typ` (access token) và `ch` (kênh phát token: `admin`/`user`). Xem mục [Authentication](#authentication).
- **Cô lập kênh:** access token lấy từ `/api/v1/auth/login` (`ch=user`) **không** gọi được `/api/v1/admin/**` nữa, kể cả tài khoản role ADMIN. Token lấy từ `/api/v1/admin/auth/login` (`ch=admin`) dùng được cả hai kênh.
- **Refresh token không dùng thay access token được nữa** — gửi refresh token qua header `Authorization` sẽ bị từ chối.
- **Access token không đọc từ cookie nữa** — chỉ header `Authorization: Bearer`. Cookie `access_token` bị bỏ hoàn toàn.
- **`POST /api/v1/auth/refresh-token`, `/logout`, `/api/v1/admin/auth/refresh-token`, `/logout`:** đổi từ "cần đăng nhập" thành **không cần access token** (trước đây trả 403 khi access token hết hạn — đúng lúc FE cần gọi refresh nhất). Chỉ cần refresh token cookie hợp lệ.
- **Error code có thể gặp thêm:**
  - `otp_max_attempts_exceeded` (429) tại `/verify-registration` và `/reset-password` — nhập sai OTP quá số lần cho phép, phải xin OTP mới.
  - `otp_resend_too_soon` (429) tại `/register` — gọi lại `/register` cùng email khi OTP cũ còn trong thời gian cooldown.
  - `email_not_verified` tại `/api/v1/admin/auth/login`.
- **`/actuator/**`** giờ yêu cầu quyền ADMIN (trước đây public). `/actuator/health` vẫn public.

### 2026-08-25 - v4.0

- Bổ sung module Account (GET/PATCH `/api/v1/account/me`, DELETE avatar)
- Bổ sung module Favorite (POST/DELETE/GET `/api/v1/me/favorites/{movieSlug}`) — thay endpoint cũ "chưa implement"
- Bổ sung module History (POST progress, GET continue-watching, GET all trên `/api/v1/me/watch-history`) — thay endpoint cũ
- Bổ sung module Membership Plan (Public GET + Admin CRUD trên `/api/v1/[admin/]membership-plans`) — thay module cũ
- Bổ sung module Payment (POST create-order, POST payos/webhook, GET status trên `/api/v1/payments`) — thay module cũ
- Bổ sung module Notification (GET list, GET unread-count, PUT read trên `/api/v1/me/notifications`) — thay endpoint cũ
- Bổ sung module Streaming (POST play movie/episode, GET master.m3u8, GET segments — token-based HLS)
- Bổ sung module Public Episode (GET `/api/v1/movies/{movieSlug}/episodes`)
- Thêm API Flows: Payment Flow (PayOS), Streaming Flow (HLS)
- Sửa enum PaymentGateway: thêm PAYOS
- Sửa enum AccountStatus: `ACTIVE, INACTIVE` (bỏ SUSPENDED, BANNED — khớp code thực tế)
- Thêm enum PaymentStatus, SortOrder
- Giữ nguyên module Comment, Rating, Report với trạng thái "chưa implement"
- Sửa Auth: verify-registration response → void (không trả body); refresh-token auth → User (cần @AuthRequired)
- Sửa User Module: xóa endpoint profile (đã chuyển sang Account Module), đổi base path → `/api/v1/admin/users`, toggle ban response → void, get all users → List (không phân trang)
- Sửa Upload Module: response field `fileUrl` → `previewUrl`
- Sửa Genre Admin: Update method PUT → PATCH
- Sửa Movie Admin: Update method PUT → PATCH
- Sửa Movie Upload: Record part done response → void
- Sửa Episode Admin: Update method PUT → PATCH, Record part done response → void
- Sửa UserProfileResult: accountStatus → `ACTIVE|INACTIVE`

### 2026-08-18 - v3.0

- Cập nhật Enums theo code thực tế: MovieType MOVIE/TV_SERIES → SINGLE/SERIES; VideoStatus → DRAFT/PROCESSING/READY/PUBLISHED/FAILED/REJECTED/BANNED; UploadType → AVATAR/POSTER/VIDEO
- Thêm enums mới: AccountStatus, OtpType, MembershipPaymentStatus, PaymentOrderStatus, PaymentGateway, NotificationType, NotificationReferenceType, ReportTargetType, ReportReason, ReportStatus
- Xóa enum MembershipStatus (FREE/PREMIUM/VIP) và RoleName (dùng UserRole trực tiếp)
- Cập nhật Auth: register request chỉ có email/password/name (không có phone); response là {userId, email, otpSent} không phải token; LoginResponse là {accessToken, expiresIn}
- Cập nhật User: UpdateProfileRequest dùng profileUrl (không phải profilePicture/birthDate); UserProfileResult cập nhật theo code (id, accountStatus thay is_active, không có membershipStatus); Ban endpoint cần body {banned: boolean}
- Cập nhật Movie/Episode DTOs: movieType dùng SINGLE/SERIES, status dùng VideoStatus mới
- Xóa module Role (không có bảng riêng, dùng enum UserRole)
- Xóa module Advertisement (chưa có entity)
- Đổi tên module Transaction → Payment (dùng payment_order, không phải transaction)
- Thêm module Notification (chưa implement)
- Thêm module Report (chưa implement)
- Cập nhật Comment: thêm episodeId?, parentId? trong request
- Cập nhật Rating: score là int, thêm review?
- Cập nhật History: đổi watchedDuration → watchDuration, thêm totalDuration, lastPosition

### 2026-08-17 - v2.0

- Cập nhật toàn bộ endpoints theo code thực tế (44 endpoints across 10 controllers)
- Thêm enums: GenreStatus, VideoStatus, VideoVisibility, UploadType
- Thêm module: Admin Auth (4 endpoints), Upload generic (1 endpoint)
- Cập nhật Genre, Movie, Episode, User modules
- Thêm Movie Video Upload module
- Cập nhật Response Format theo ApiResponseDto flat envelope

### 2026-07-21 - v1.0 (Initial)

- Created fe_api.md with 12 modules
- Total: 50+ endpoints documented
