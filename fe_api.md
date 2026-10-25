# FE_API.md - API Contract

> **Mục đích:** Tài liệu API cho Frontend - danh sách endpoint, request/response, flow

**Cập nhật lần cuối:** 2026-10-24  
**Version:** 4.8

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

### AgeRating

```json
["P", "K", "T13", "T16", "T18", "C"]
```

### VideoQuality

```json
["HD", "FULL_HD", "FOUR_K"]
```

### AudioTrackType

```json
["VIETSUB", "DUBBED", "VOICEOVER", "MULTI_AUDIO"]
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

| STT | Chức năng        | Method | Path                                                   | Auth  | Request                             | Response               |
| --- | ---------------- | ------ | ------------------------------------------------------ | ----- | ----------------------------------- | ---------------------- |
| 1   | Create genre     | POST   | `/api/v1/admin/genres`                                 | Admin | name, icon?, description?           | GenreResult            |
| 2   | Update genre     | PATCH  | `/api/v1/admin/genres/{id}`                            | Admin | name?, icon?, description?, status? | GenreResult            |
| 3   | Get genre detail | GET    | `/api/v1/admin/genres/{slug}`                          | Admin | -                                   | GenreResult            |
| 4   | Get all genres   | GET    | `/api/v1/admin/genres?search=&status=&page=1&limit=10` | Admin | search, status, page, limit         | GenreList + pagination |
| 5   | Delete genre     | DELETE | `/api/v1/admin/genres/{id}`                            | Admin | -                                   | void                   |

> Admin GET detail dùng slug (hỗ trợ fallback UUID). Admin list trả tất cả status (hoặc lọc theo param status).

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

## ACTOR Module (Admin)

| STT | Chức năng           | Method | Path                                                        | Auth  | Request                                           | Response               |
| --- | ------------------- | ------ | ----------------------------------------------------------- | ----- | ------------------------------------------------- | ---------------------- |
| 1   | Tạo mới diễn viên   | POST   | `/api/v1/admin/actors`                                      | Admin | CreateActorRequest                                | ActorResult            |
| 2   | Cập nhật diễn viên  | PATCH  | `/api/v1/admin/actors/{id}`                                 | Admin | UpdateActorRequest                                | ActorResult            |
| 3   | Chi tiết diễn viên  | GET    | `/api/v1/admin/actors/{id}`                                 | Admin | -                                                 | ActorResult            |
| 4   | Danh sách diễn viên | GET    | `/api/v1/admin/actors?search=&nationality=&page=1&limit=10` | Admin | search, nationality, page, limit, sortBy, sortDir | ActorList + pagination |
| 5   | Xoá mềm diễn viên   | DELETE | `/api/v1/admin/actors/{id}`                                 | Admin | -                                                 | void                   |
| 6   | Upload avatar       | POST   | `/api/v1/admin/actors/{id}/avatar`                          | Admin | file (multipart)                                  | ActorResult            |

**ActorResult DTO:**

```json
{
  "id": "uuid",
  "name": "string",
  "originalName": "string",
  "avatarUrl": "url (tự động resolve từ MinIO actor-avatars)",
  "bio": "string",
  "birthDate": "YYYY-MM-DD",
  "nationality": "string",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

**Request DTOs:**

```json
{
  "CreateActorRequest": {
    "name": "string (required, max 255)",
    "originalName": "string (max 255)",
    "avatarUrl": "string (optional)",
    "bio": "string (optional)",
    "birthDate": "YYYY-MM-DD (optional)",
    "nationality": "string (max 100, optional)"
  },
  "UpdateActorRequest": {
    "name": "string (max 255, optional)",
    "originalName": "string (max 255, optional)",
    "avatarUrl": "string (optional)",
    "bio": "string (optional)",
    "birthDate": "YYYY-MM-DD (optional)",
    "nationality": "string (max 100, optional)"
  }
}
```

---

## MOVIE Module (Public & Admin)

### Public

| STT | Chức năng        | Method | Path                                                                                                   | Auth   | Request                                                                           | Response               |
| --- | ---------------- | ------ | ------------------------------------------------------------------------------------------------------ | ------ | --------------------------------------------------------------------------------- | ---------------------- |
| 1   | Get all movies   | GET    | `/api/v1/movies?search=&type=&genreId=&year=&country=&ageRating=&quality=&audioTrack=&page=1&limit=20` | Public | search, type, genreId, year, country, ageRating, quality, audioTrack, page, limit | MovieList + pagination |
| 2   | Get movie detail | GET    | `/api/v1/movies/{slug}`                                                                                | Public | -                                                                                 | MovieDetailResult      |

> Public chỉ trả movie không bị soft-delete. Detail kèm episodes (nếu SERIES).

### Admin

| STT | Chức năng        | Method | Path                                                                                                                 | Auth  | Request                                                                                                                                                                                                                                                                                                                                               | Response               |
| --- | ---------------- | ------ | -------------------------------------------------------------------------------------------------------------------- | ----- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- |
| 1   | Create movie     | POST   | `/api/v1/admin/movies`                                                                                               | Admin | title, originalTitle?, description?, releaseYear?, duration?, director?, movieActors? [{ actorId, characterName?, orderIndex? }], country?, language?, trailerUrl?, movieType (SINGLE/SERIES - default SINGLE), totalEpisodes?, ageRating? (P/K/T13/T16/T18/C - default T18), videoQualities? (default HD), audioTracks? (default VIETSUB), genreIds? | MovieResult            |
| 2   | Update movie     | PATCH  | `/api/v1/admin/movies/{id}`                                                                                          | Admin | (all fields optional: title?, originalTitle?, description?, releaseYear?, duration?, director?, movieActors? [{ actorId, characterName?, orderIndex? }], country?, language?, trailerUrl?, movieType?, totalEpisodes?, ageRating?, videoQualities?, audioTracks?, isFeatured?, isPremium?, status?, visibility?, genreIds?)                           | MovieResult            |
| 3   | Get movie detail | GET    | `/api/v1/admin/movies/{slug}`                                                                                        | Admin | -                                                                                                                                                                                                                                                                                                                                                     | MovieDetailResult      |
| 4   | Get all movies   | GET    | `/api/v1/admin/movies?search=&type=&status=&genreId=&year=&country=&ageRating=&quality=&audioTrack=&page=1&limit=10` | Admin | search, type, status, genreId, year, country, ageRating, quality, audioTrack, page, limit                                                                                                                                                                                                                                                             | MovieList + pagination |
| 5   | Delete movie     | DELETE | `/api/v1/admin/movies/{id}`                                                                                          | Admin | -                                                                                                                                                                                                                                                                                                                                                     | void                   |
| 6   | Restore movie    | PATCH  | `/api/v1/admin/movies/{id}/restore`                                                                                  | Admin | -                                                                                                                                                                                                                                                                                                                                                     | MovieResult            |
| 7   | Upload poster    | POST   | `/api/v1/admin/movies/{id}/poster`                                                                                   | Admin | file (multipart)                                                                                                                                                                                                                                                                                                                                      | MovieResult            |
| 8   | Upload backdrop  | POST   | `/api/v1/admin/movies/{id}/backdrop`                                                                                 | Admin | file (multipart)                                                                                                                                                                                                                                                                                                                                      | MovieResult            |
| 9   | Publish movie    | POST   | `/api/v1/admin/movies/{id}/publish`                                                                                  | Admin | -                                                                                                                                                                                                                                                                                                                                                     | MovieResult            |

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
  "movieActors": [
    {
      "actorId": "string",
      "name": "string",
      "originalName": "string",
      "avatarUrl": "url",
      "nationality": "string",
      "characterName": "string",
      "orderIndex": 0
    }
  ],
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
  "ratingCount": "number",
  "commentCount": "number",
  "viewCount": "number",
  "ageRating": "P|K|T13|T16|T18|C",
  "videoQualities": "HD,FULL_HD,FOUR_K",
  "audioTracks": "VIETSUB,DUBBED,VOICEOVER,MULTI_AUDIO",
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
  "createdByName": "string",
  "isFavorite": "boolean | null (true nếu user đã thêm vào yêu thích, false nếu chưa, null nếu là khách vãng lai)"
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
  },
  "userProfile": {
    "response": {
      "id": "string",
      "email": "string",
      "name": "string",
      "profileUrl": "string (presigned URL)",
      "phone": "string",
      "address": "string",
      "role": "ADMIN | USER",
      "accountStatus": "ACTIVE | INACTIVE",
      "hasActiveMembership": true,
      "isVip": true,
      "currentPlanName": "string | null (ví dụ: Gói Tháng)",
      "membershipEndDate": "string | null (ví dụ: 2026-10-22)",
      "createdAt": "datetime"
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

| STT | Chức năng             | Method | Path                                         | Auth | Request                    | Response                   |
| --- | --------------------- | ------ | -------------------------------------------- | ---- | -------------------------- | -------------------------- |
| 1   | Cập nhật tiến độ xem  | POST   | `/api/v1/me/watch-history/progress`          | User | UpdateWatchProgressRequest | void                       |
| 2   | Xem tiếp (continue)   | GET    | `/api/v1/me/watch-history/continue-watching` | User | -                          | List\<WatchHistoryResult\> |
| 3   | Toàn bộ lịch sử       | GET    | `/api/v1/me/watch-history`                   | User | -                          | List\<WatchHistoryResult\> |
| 4   | Xoá 1 bản ghi lịch sử | DELETE | `/api/v1/me/watch-history/{id}`              | User | -                          | void                       |
| 5   | Xoá toàn bộ lịch sử   | DELETE | `/api/v1/me/watch-history`                   | User | -                          | void                       |

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

## MEMBERSHIP Module (User)

| STT | Chức năng           | Method | Path                    | Auth | Request | Response           | Note                                         |
| --- | ------------------- | ------ | ----------------------- | ---- | ------- | ------------------ | -------------------------------------------- |
| 1   | Xem gói VIP của tôi | GET    | `/api/v1/me/membership` | User | -       | MyMembershipResult | Trả về thông tin gói đang kích hoạt (active) |

**Response DTO:**

```json
{
  "myMembership": {
    "planName": "string (ví dụ: Gói Tháng)",
    "planSlug": "string (ví dụ: goi-thang)",
    "price": 79000.0,
    "maxDevices": 2,
    "canDownload": true,
    "videoQuality": "4K Ultra HD",
    "startDate": "2026-10-01",
    "endDate": "2026-10-31",
    "daysRemaining": 8,
    "isExpired": false
  }
}
```

---

## PAYMENT Module (User)

| STT | Chức năng           | Method | Path                                        | Auth   | Request                                   | Response                                |
| --- | ------------------- | ------ | ------------------------------------------- | ------ | ----------------------------------------- | --------------------------------------- |
| 1   | Tạo đơn thanh toán  | POST   | `/api/v1/payments/create-order`             | User   | CreatePaymentRequest                      | PaymentOrderResult (201)                |
| 2   | PayOS webhook       | POST   | `/api/v1/payments/payos/webhook`            | Public | raw JSON (PayOS)                          | void                                    |
| 3   | Kiểm tra trạng thái | GET    | `/api/v1/payments/{transactionCode}/status` | User   | -                                         | PaymentOrderResult                      |
| 4   | Lịch sử đơn của tôi | GET    | `/api/v1/payments/my-orders`                | User   | phân trang (page, limit, sortBy, sortDir) | List\<PaymentOrderResult\> + pagination |

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

## COMMENT Module (User & Admin)

Bình luận **lồng nhau không giới hạn cấp**: một bình luận trả lời được bình luận khác ở bất kỳ cấp nào. Danh sách **phân trang theo bình luận gốc** — `pagination.totalItems` là số luồng, không phải tổng số bình luận; toàn bộ trả lời của các gốc trong trang nằm lồng trong trường `replies`.

| STT | Chức năng         | Method | Path                                                                              | Auth               | Request                        | Response                      | Note                                       |
| --- | ----------------- | ------ | --------------------------------------------------------------------------------- | ------------------ | ------------------------------ | ----------------------------- | ------------------------------------------ |
| 1   | Cây bình luận     | GET    | `/api/v1/movies/{movieSlug}/comments?episodeId=&page=1&limit=15&sortBy=&sortDir=` | Public             | episodeId?, phân trang         | CommentList + pagination      | Gọi kèm token thì có thêm cờ `isLiked`     |
| 2   | Đăng bình luận    | POST   | `/api/v1/movies/{movieSlug}/comments`                                             | User               | content, episodeId?, parentId? | CommentDetail                 | 201                                        |
| 3   | Sửa bình luận     | PATCH  | `/api/v1/comments/{commentId}`                                                    | Chủ sở hữu         | content                        | CommentDetail                 | Người khác sửa → 403 `comment_not_owned`   |
| 4   | Xoá bình luận     | DELETE | `/api/v1/comments/{commentId}`                                                    | Chủ sở hữu / Admin | -                              | -                             | Xoá kéo theo **cả nhánh trả lời bên dưới** |
| 5   | Thích             | POST   | `/api/v1/comments/{commentId}/like`                                               | User               | -                              | -                             | 201. Thích 2 lần → 409                     |
| 6   | Bỏ thích          | DELETE | `/api/v1/comments/{commentId}/like`                                               | User               | -                              | -                             | Chưa thích mà bỏ → 404                     |
| 7   | DS kiểm duyệt     | GET    | `/api/v1/admin/comments?movieId=&isApproved=&search=&page=1&limit=15`             | Admin              | movieId?, isApproved?, search? | AdminCommentList + pagination | **Phẳng**, không dựng cây                  |
| 8   | Ẩn / hiện lại     | PATCH  | `/api/v1/admin/comments/{commentId}/approval`                                     | Admin              | isApproved (bool)              | -                             | Xem lưu ý bên dưới                         |
| 9   | Bình luận của tôi | GET    | `/api/v1/me/comments?page=1&limit=15&sortBy=&sortDir=`                            | User               | phân trang                     | MyCommentList + pagination    | Lịch sử bình luận của chính mình           |

### Lưu ý nghiệp vụ

- **`episodeId`**: bỏ trống là bình luận cấp phim; truyền vào là bình luận của đúng tập đó. Hai nhóm **không lẫn vào nhau**. Khi có `parentId`, giá trị `episodeId` trong body **bị bỏ qua** — bình luận con luôn kế thừa phạm vi của bình luận cha.
- **`parentId`** trỏ được tới bình luận ở bất kỳ cấp nào. Nếu bình luận cha thuộc phim khác → 400 `comment_parent_mismatch`.
- **Tự động duyệt**: bình luận hiện ngay sau khi đăng, không cần admin phê duyệt.
- **Ẩn một bình luận là ẩn cả nhánh con của nó.** Cây được dựng từ gốc trở xuống, nên con của một nút bị ẩn trở thành mồ côi và cũng không hiện ra.
- **Thông báo**: trả lời bình luận của người khác sẽ sinh notification `COMMENT_REPLY` với `referenceType = COMMENT`. Tự trả lời chính mình thì không.
- **`likeCount` có thể trễ tối đa vài phút** so với cột trong database (số lượt thích đếm ở Redis rồi gộp xuống định kỳ), nhưng **luôn đúng với số lượt thích thật** — nguồn sự thật là bảng `comment_like`.

### CommentDetail

```json
{
  "id": "string",
  "content": "string",
  "parentId": "string | null",
  "author": {
    "id": "string",
    "name": "string",
    "avatarUrl": "string (presigned URL) | null"
  },
  "likeCount": 12,
  "isLiked": false,
  "createdAt": "datetime",
  "updatedAt": "datetime",
  "replies": ["CommentDetail (đệ quy, không giới hạn cấp)"]
}
```

### AdminCommentDetail

```json
{
  "id": "string",
  "content": "string",
  "movieId": "string",
  "episodeId": "string | null",
  "parentId": "string | null",
  "author": { "id": "string", "name": "string", "avatarUrl": "string | null" },
  "isApproved": true,
  "likeCount": 12,
  "createdAt": "datetime"
}
```

---

## RATING Module (User)

Chấm **sao, thang 1–5, số nguyên**. Mỗi người chỉ có **một** đánh giá cho một phim; gọi lại `PUT` là ghi đè. Điểm trung bình hiển thị có phần thập phân (ví dụ `3.5` để FE tô 3 sao rưỡi).

| STT | Chức năng               | Method | Path                                                                  | Auth   | Request        | Response                  | Note                                   |
| --- | ----------------------- | ------ | --------------------------------------------------------------------- | ------ | -------------- | ------------------------- | -------------------------------------- |
| 1   | DS đánh giá của phim    | GET    | `/api/v1/movies/{movieSlug}/ratings?page=1&limit=15&sortBy=&sortDir=` | Public | phân trang     | RatingList + pagination   | -                                      |
| 2   | Tóm tắt điểm            | GET    | `/api/v1/movies/{movieSlug}/ratings/summary`                          | Public | -              | RatingSummary             | -                                      |
| 3   | Chấm / sửa điểm         | PUT    | `/api/v1/me/ratings/{movieSlug}`                                      | User   | score, review? | RatingDetail              | Tạo mới hoặc ghi đè                    |
| 4   | Điểm của tôi            | GET    | `/api/v1/me/ratings/{movieSlug}`                                      | User   | -              | RatingDetail (hoặc null)  | Chưa chấm → 200 OK với `data: null`    |
| 5   | Xoá điểm của tôi        | DELETE | `/api/v1/me/ratings/{movieSlug}`                                      | User   | -              | -                         | Xoá xong **chấm lại được bình thường** |
| 6   | DS phim tôi đã đánh giá | GET    | `/api/v1/me/ratings?page=1&limit=15&sortBy=&sortDir=`                 | User   | phân trang     | MyRatingList + pagination | Lịch sử đánh giá của chính mình        |

### Lưu ý nghiệp vụ

- `score` ngoài khoảng 1–5 hoặc không phải số nguyên → 400 `validation_failed`.
- Mỗi lần chấm / sửa / xoá, trường `rating` của phim (API `GET /api/v1/movies/{slug}`) được **tính lại ngay** từ toàn bộ đánh giá, làm tròn 1 chữ số thập phân.
- Xoá đánh giá cuối cùng của một phim → `rating` của phim về `0.0`, không phải `null`.

### RatingDetail / RatingSummary

```json
{
  "ratingDetail": {
    "id": "string",
    "movieId": "string",
    "score": 4,
    "review": "string | null",
    "author": {
      "id": "string",
      "name": "string",
      "avatarUrl": "string | null"
    },
    "createdAt": "datetime",
    "updatedAt": "datetime"
  },
  "ratingSummary": {
    "averageScore": 3.5,
    "totalRatings": 128
  }
}
```

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

### 2026-10-23 - v4.7 - Gói VIP của tôi + Xoá lịch sử xem + Lịch sử đơn hàng + Cờ VIP/Favorite

#### 1. Endpoint MỚI

| Endpoint                        | Method | Auth | Mô tả                                                                                              |
| ------------------------------- | ------ | ---- | -------------------------------------------------------------------------------------------------- |
| `/api/v1/me/membership`         | GET    | User | Xem thông tin gói VIP đang kích hoạt (ngày hết hạn, số ngày còn lại, quyền lợi)                    |
| `/api/v1/payments/my-orders`    | GET    | User | Lịch sử các đơn thanh toán PayOS của chính mình (phân trang: `page`, `limit`, `sortBy`, `sortDir`) |
| `/api/v1/me/watch-history/{id}` | DELETE | User | Xoá 1 bản ghi lịch sử xem phim                                                                     |
| `/api/v1/me/watch-history`      | DELETE | User | Xoá toàn bộ lịch sử xem phim của tài khoản                                                         |

#### 2. Cập nhật DTO & Hành vi

- **`GET /api/v1/account/me` (`UserProfileResult`)**: Bổ sung `isVip: boolean`, `currentPlanName: string | null`, `membershipEndDate: string | null` (FE hiển thị huy hiệu VIP và tên gói nhanh chóng).
- **`GET /api/v1/movies/{id}` & `GET /api/v1/movies/{slug}` (`MovieDetailResult`)**: Bổ sung `isFavorite: boolean | null` (FE không cần gọi danh sách `/me/favorites` để dò trạng thái yêu thích).
- **`GET /api/v1/me/ratings/{movieSlug}`**: Khi người dùng chưa đánh giá, API trả về **HTTP 200 OK với `data: null`** (thay vì quăng exception 404 `rating_not_found` gây đỏ Network tab).

### 2026-10-14 - v4.6 - Module Comment (lồng nhau) + Rating (1–5 sao)

Hai module cuối cùng đang để trạng thái "chưa implement" nay đã chạy. **13 endpoint mới**, không có endpoint cũ nào đổi hay mất.

#### Comment — lồng nhau không giới hạn cấp

| Endpoint                                      | Method | Auth             |
| --------------------------------------------- | ------ | ---------------- |
| `/api/v1/movies/{movieSlug}/comments`         | GET    | Public           |
| `/api/v1/movies/{movieSlug}/comments`         | POST   | User             |
| `/api/v1/comments/{commentId}`                | PATCH  | Chủ sở hữu       |
| `/api/v1/comments/{commentId}`                | DELETE | Chủ sở hữu/Admin |
| `/api/v1/comments/{commentId}/like`           | POST   | User             |
| `/api/v1/comments/{commentId}/like`           | DELETE | User             |
| `/api/v1/admin/comments`                      | GET    | Admin            |
| `/api/v1/admin/comments/{commentId}/approval` | PATCH  | Admin            |

Ba điểm FE cần biết trước khi dựng giao diện:

1. **`pagination.totalItems` đếm theo bình luận GỐC**, không phải tổng số bình luận. Trả lời nằm lồng trong `replies`, không tính vào phân trang.
2. **Xoá hoặc ẩn một bình luận là xoá/ẩn cả nhánh trả lời bên dưới nó.** Không có trạng thái "[đã xoá]" giữ chỗ.
3. **Gọi `GET` không token vẫn chạy**, chỉ là `isLiked` luôn `false` cho mọi bình luận.

#### Rating — thang 1–5 sao

| Endpoint                                     | Method | Auth   |
| -------------------------------------------- | ------ | ------ |
| `/api/v1/movies/{movieSlug}/ratings`         | GET    | Public |
| `/api/v1/movies/{movieSlug}/ratings/summary` | GET    | Public |
| `/api/v1/me/ratings/{movieSlug}`             | PUT    | User   |
| `/api/v1/me/ratings/{movieSlug}`             | GET    | User   |
| `/api/v1/me/ratings/{movieSlug}`             | DELETE | User   |

- `score` là **số nguyên 1–5**. Phần thập phân chỉ xuất hiện ở `averageScore` của `summary` và ở trường `rating` của phim (ví dụ `3.5` để tô 3 sao rưỡi).
- Chấm điểm xong thì trường `rating` trong `GET /api/v1/movies/{slug}` **đổi ngay**, không chờ.
- Xoá đánh giá rồi **chấm lại được bình thường**.

#### Mã lỗi mới

`comment_not_found`, `comment_not_owned`, `comment_parent_not_found`, `comment_parent_mismatch`, `comment_already_liked`, `comment_like_not_found`, `rating_not_found`, `rating_score_invalid`.

#### Module Report vẫn chưa implement

### 2026-10-04 - v4.5 - Chuẩn hoá phân trang cho MỌI API danh sách

#### Tham số chung — áp cho tất cả API lấy danh sách

| Tham số   | Mặc định   | Ghi chú                                                   |
| --------- | ---------- | --------------------------------------------------------- |
| `page`    | `1`        | Đếm **từ 1**                                              |
| `limit`   | **`15`**   | Trần tối đa **50**; xin lớn hơn thì bị ép về 50           |
| `search`  | —          | Không truyền thì không lọc                                |
| `sortBy`  | tuỳ module | Phải nằm trong danh sách hợp lệ bên dưới, sai thì **400** |
| `sortDir` | `desc`     | `asc` hoặc `desc`                                         |

```
GET /api/v1/movies?page=2&limit=20&search=co%20dau&sortBy=title&sortDir=asc
```

Không truyền gì thì mặc định là trang 1, 15 dòng, sắp xếp theo field mặc định của module, giảm dần.

#### Field được phép sắp xếp theo module

| Module                            | `sortBy` hợp lệ                                            | Mặc định        |
| --------------------------------- | ---------------------------------------------------------- | --------------- |
| Movie (public + admin)            | `createdAt`, `title`, `viewCount`, `rating`, `releaseYear` | `createdAt`     |
| Genre (public + admin)            | `createdAt`, `name`                                        | `createdAt`     |
| User (admin)                      | `createdAt`, `email`, `name`, `lastActiveAt`               | `createdAt`     |
| Episode                           | `episodeNumber`, `createdAt`, `title`                      | `episodeNumber` |
| Membership Plan                   | `createdAt`, `name`, `price`, `durationDays`               | `price`         |
| Notification                      | `createdAt`, `isRead`                                      | `createdAt`     |
| Watch History / Continue Watching | `watchedAt`, `progress`                                    | `watchedAt`     |
| Favorite                          | `createdAt`                                                | `createdAt`     |
| Comment (public + admin)          | `createdAt`, `likeCount`                                   | `createdAt`     |
| Rating                            | `createdAt`, `score`                                       | `createdAt`     |
| Session                           | `lastUsedAt` (cố định, không đổi được)                     | `lastUsedAt`    |

`sortBy` ngoài danh sách trả **400** với `code: bad_request`.

#### Endpoint MỚI có phân trang (trước đây trả mảng trần)

- `GET /api/v1/admin/movies/{movieId}/episodes`
- `GET /api/v1/movies/{movieSlug}/episodes`
- `GET /api/v1/me/favorites`
- `GET /api/v1/admin/membership-plans`
- `GET /api/v1/membership-plans`
- `GET /api/v1/me/watch-history/continue-watching`
- `GET /api/v1/account/sessions`

> **FE cũ KHÔNG vỡ:** `data` vẫn là mảng như trước, chỉ thêm `pagination` (trước là `null`) và mảng bị giới hạn 15 phần tử đầu. Muốn lấy tiếp thì truyền `page=2`.

#### Tham số MỚI trên endpoint đã có

| Endpoint                       | Thêm                                                 |
| ------------------------------ | ---------------------------------------------------- |
| `GET /api/v1/admin/users`      | `search` (email, tên)                                |
| `GET /api/v1/me/notifications` | `search` (tiêu đề, nội dung) + `isRead` (true/false) |
| Tất cả endpoint danh sách      | `sortBy`, `sortDir`                                  |

#### ⚠️ Thay đổi cần để ý

| Thay đổi                                | Ảnh hưởng                                                                                                                                                                   |
| --------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `limit` mặc định **10 hoặc 20 → 15**    | Danh sách trả về ít/nhiều hơn trước nếu FE không truyền `limit`                                                                                                             |
| `continue-watching` **sửa lỗi đếm sai** | Trước đây lấy 10 bản ghi rồi mới loại phim đã xem xong, nên có khi chỉ trả về 3-4 phim. Nay lọc ngay trong truy vấn → trả **đủ** số lượng yêu cầu và `totalItems` chính xác |
| `sortBy` sai → **400**                  | Nếu FE đang truyền `sortBy` tuỳ ý thì phải đổi sang field trong bảng trên                                                                                                   |

#### `search` cho favorites và watch-history (bổ sung 2026-10-05)

Hai endpoint dưới đây giờ nhận `search`, **lọc theo TÊN PHIM**:

| Endpoint                       | `search` lọc theo |
| ------------------------------ | ----------------- |
| `GET /api/v1/me/favorites`     | tên phim          |
| `GET /api/v1/me/watch-history` | tên phim          |

```
GET /api/v1/me/favorites?search=co%20dau&page=1&limit=15
```

Kết quả chỉ nằm **trong** danh sách yêu thích / lịch sử xem của chính người dùng — không phải tìm trên toàn bộ kho phim. Không truyền `search` thì không lọc, hành vi như trước.

`GET /api/v1/me/watch-history/continue-watching` **không** có `search` (danh sách ngắn, không cần).

> 🔴 **Sửa lỗi `addedAt` ở `me/favorites`:** trước đây trả **ngày tạo phim**, nay trả đúng **ngày người dùng thêm phim vào yêu thích**. Nếu FE đang hiển thị "Đã thêm ..." thì giá trị sẽ khác trước — đây là sửa lỗi, không phải breaking change.

### 2026-10-04 - v4.4 - Quản lý phiên đăng nhập + mật khẩu tách ngưỡng + claim `ver`

#### Endpoint MỚI — Quản lý thiết bị đang đăng nhập

| Endpoint                               | Method | Auth | Response              |
| -------------------------------------- | ------ | ---- | --------------------- |
| `/api/v1/account/sessions`             | GET    | User | `List<SessionResult>` |
| `/api/v1/account/sessions/{sessionId}` | DELETE | User | 204 No Content        |
| `/api/v1/account/sessions/other`       | DELETE | User | 204 No Content        |

**`SessionResult`:**

```json
{
  "sessionId": "9f2c1a8e-...",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0) ... Chrome/120",
  "ipAddress": "203.0.113.9",
  "admin": false,
  "current": true,
  "lastUsedAt": "2026-10-04T09:12:00",
  "createdAt": "2026-10-01T20:30:00",
  "expiresAt": "2026-10-31T20:30:00"
}
```

- `current: true` là **thiết bị đang dùng** — FE nên gắn nhãn "Thiết bị này" và ẩn nút đăng xuất cho dòng đó.
- Danh sách chỉ gồm phiên **còn hiệu lực** (chưa đăng xuất, chưa hết hạn). Mỗi lần đăng nhập trên một thiết bị tạo một phiên; refresh token xoay vòng mỗi 15 phút **không** tạo phiên mới.
- `DELETE` chỉ đăng xuất được phiên **của chính mình**. Phiên của người khác và phiên không tồn tại đều trả `resource_not_found` (404).

#### Thay đổi — mật khẩu

| Endpoint                            | Ngưỡng tối thiểu                              |
| ----------------------------------- | --------------------------------------------- |
| `POST /api/v1/auth/register`        | **8 ký tự** (trước là 6)                      |
| `POST /api/v1/auth/change-password` | tài khoản thường **8**, tài khoản admin **6** |
| `POST /api/v1/auth/reset-password`  | tài khoản thường **8**, tài khoản admin **6** |

Mã lỗi mới: `password_too_short` (400).

> Mật khẩu đã lưu không bị ảnh hưởng — ngưỡng chỉ áp khi **đặt mật khẩu mới**, không áp lúc đăng nhập.

#### Thay đổi — access token chết ngay khi đổi mật khẩu

Trước đây đổi mật khẩu chỉ thu hồi refresh token, access token cũ vẫn chạy tới 15 phút. Nay access token mang claim `ver`; đổi mật khẩu làm token cũ **bị từ chối ngay**.

**FE cần làm:** sau khi gọi `change-password` hoặc `reset-password` thành công, gọi `/refresh-token` để lấy access token mới (hoặc cho người dùng đăng nhập lại). Nếu dùng tiếp token cũ sẽ nhận 401/403.

> Không breaking với token phát trước bản này: thiếu claim `ver` được coi là `0`, khớp với người dùng chưa từng đổi mật khẩu.

#### Thay đổi — dùng lại refresh token đã thu hồi

Gửi lại một refresh token đã đổi (quá **10 giây**) bị coi là dấu hiệu token bị đánh cắp → **toàn bộ phiên đó bị đăng xuất**.

**FE cần làm:** đảm bảo chỉ có **một** request refresh tại một thời điểm (gộp vào một promise dùng chung). Hai tab cùng refresh trong vòng 10 giây vẫn an toàn nhờ cửa sổ ân hạn, nhưng ngoài khoảng đó sẽ bị đăng xuất.

#### Hiệu năng — không đổi contract

Các endpoint `/me/favorites`, `/me/watch-history`, `/me/notifications` và nhóm playback giữ nguyên request/response, chỉ chạy nhanh hơn: bỏ một truy vấn thừa mỗi request và gỡ N+1 ở danh sách phim yêu thích (300 phim yêu thích từng tốn 301 truy vấn, nay còn 2).

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
