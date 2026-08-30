# DATABASE.md - Schema & Relationships

> **Mục đích:** Tài liệu database - schema, index, relation, data flow

**Cập nhật lần cuối:** 2026-08-25  
**Database:** MySQL 8.0+  
**Charset:** UTF-8

---

## QUY ĐỊNH

- Hiển thị theo thứ tự logical (domain flow)
- Hiển thị các Indexes, Constraints, Relationships
- Table format: STT, Column, Type, Constraint, Description
- Theo dõi data flow: User → Movie → Episode → Comment/Rating → Favorite/History

---

## Danh sách bảng

| STT | Bảng                 | Mục đích                     | Rows dự kiến |
| --- | -------------------- | ---------------------------- | ------------ |
| 1   | `user`               | Người dùng                   | ~10k         |
| 2   | `genre`              | Thể loại phim                | ~100         |
| 3   | `movie`              | Thông tin phim               | ~1k          |
| 4   | `movie_genre`        | Liên kết phim - thể loại     | ~5k          |
| 5   | `episode`            | Tập phim (series)            | ~5k          |
| 6   | `upload_session`     | Phiên upload video resumable | ~100         |
| 7   | `upload_part`        | Part đã upload trong phiên   | ~5k          |
| 8   | `comment`            | Bình luận                    | ~100k        |
| 9   | `rating`             | Đánh giá                     | ~50k         |
| 10  | `list_favorite`      | Danh sách yêu thích          | ~100k        |
| 11  | `history_watching`   | Lịch sử xem                  | ~500k        |
| 12  | `membership_plan`    | Gói thành viên               | ~5           |
| 13  | `membership`         | Đăng ký thành viên           | ~5k          |
| 14  | `payment_order`      | Đơn thanh toán               | ~5k          |
| 15  | `refresh_token`      | Refresh token session        | ~10k         |
| 16  | `otp_verification`   | OTP xác thực                 | ~10k         |
| 17  | `email_verification` | Xác thực email bằng token    | ~10k         |
| 18  | `notification`       | Thông báo                    | ~50k         |
| 19  | `report`             | Báo cáo vi phạm              | ~1k          |
| 20  | `outbox_event`       | Outbox pattern cho event     | ~10k         |

---

## Chi tiết các bảng

### 1. user (Người dùng)

| STT | Column                | Type                   | Constraint                | Description               |
| --- | --------------------- | ---------------------- | ------------------------- | ------------------------- |
| 1   | id                    | UUID (String)          | PRIMARY KEY               | ID người dùng             |
| 2   | email                 | VARCHAR(255)           | NOT NULL, UNIQUE          | Email đăng ký             |
| 3   | password              | VARCHAR(255)           | NOT NULL                  | Mật khẩu hash             |
| 4   | name                  | VARCHAR(100)           | NOT NULL                  | Tên người dùng            |
| 5   | profile_url           | VARCHAR(500)           | -                         | URL ảnh đại diện          |
| 6   | phone                 | VARCHAR(20)            | UNIQUE                    | Số điện thoại             |
| 7   | address               | TEXT                   | -                         | Địa chỉ                   |
| 8   | role                  | ENUM(ADMIN, USER)      | NOT NULL                  | Quyền hạn (UserRole enum) |
| 9   | email_verified        | BOOLEAN                | -                         | Email đã xác thực         |
| 10  | account_status        | ENUM(ACTIVE, INACTIVE) | -                         | Trạng thái tài khoản      |
| 11  | failed_login_attempts | INT                    | -                         | Số lần đăng nhập sai      |
| 12  | first_failure_at      | TIMESTAMP              | -                         | Thời điểm sai lần đầu     |
| 13  | locked_until          | TIMESTAMP              | -                         | Khóa tạm tới thời điểm    |
| 14  | is_banned             | BOOLEAN                | NOT NULL, DEFAULT false   | Đã bị cấm                 |
| 15  | last_active_at        | TIMESTAMP              | -                         | Lần hoạt động cuối        |
| 16  | deleted_at            | TIMESTAMP              | -                         | Soft delete               |
| 17  | created_at            | TIMESTAMP              | DEFAULT CURRENT_TIMESTAMP | Ngày tạo                  |
| 18  | updated_at            | TIMESTAMP              | -                         | Ngày cập nhật             |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- UNIQUE: email, phone
- INDEX: idx_user_account_status (account_status)
- INDEX: idx_user_deleted_at (deleted_at)

> Role dùng ENUM trực tiếp, không có bảng `role` riêng.

---

### 2. genre (Thể loại phim)

| STT | Column      | Type                   | Constraint                | Description       |
| --- | ----------- | ---------------------- | ------------------------- | ----------------- |
| 1   | id          | UUID (String)          | PRIMARY KEY               | ID thể loại       |
| 2   | name        | VARCHAR(100)           | NOT NULL, UNIQUE          | Tên thể loại      |
| 3   | slug        | VARCHAR(120)           | NOT NULL, UNIQUE          | Slug SEO-friendly |
| 4   | icon        | VARCHAR(255)           | -                         | Icon thể loại     |
| 5   | description | TEXT                   | -                         | Mô tả             |
| 6   | status      | ENUM(ACTIVE, INACTIVE) | NOT NULL, length=20       | Trạng thái        |
| 7   | deleted_at  | TIMESTAMP              | -                         | Soft delete       |
| 8   | created_at  | TIMESTAMP              | DEFAULT CURRENT_TIMESTAMP | Ngày tạo          |
| 9   | updated_at  | TIMESTAMP              | -                         | Ngày cập nhật     |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- UNIQUE: name, slug
- INDEX: idx_genre_status (status)
- INDEX: idx_genre_deleted_at (deleted_at)

> Slug auto-generate từ name: "Hành Động" → "hanh-dong" (dùng SlugUtils)

---

### 3. movie (Phim)

| STT | Column              | Type                                                                | Constraint                | Description                       |
| --- | ------------------- | ------------------------------------------------------------------- | ------------------------- | --------------------------------- |
| 1   | id                  | UUID (String)                                                       | PRIMARY KEY               | ID phim                           |
| 2   | slug                | VARCHAR(255)                                                        | NOT NULL, UNIQUE          | Slug SEO-friendly                 |
| 3   | title               | VARCHAR(255)                                                        | NOT NULL                  | Tên phim                          |
| 4   | original_title      | VARCHAR(255)                                                        | -                         | Tên gốc                           |
| 5   | description         | TEXT                                                                | -                         | Mô tả                             |
| 6   | release_year        | YEAR                                                                | -                         | Năm phát hành                     |
| 7   | duration            | INT                                                                 | -                         | Thời lượng (phút)                 |
| 8   | director            | VARCHAR(255)                                                        | -                         | Đạo diễn                          |
| 9   | actors              | TEXT                                                                | -                         | Diễn viên                         |
| 10  | country             | VARCHAR(100)                                                        | -                         | Quốc gia                          |
| 11  | language            | VARCHAR(50)                                                         | -                         | Ngôn ngữ                          |
| 12  | trailer_url         | VARCHAR(500)                                                        | -                         | URL trailer                       |
| 13  | poster_url          | VARCHAR(500)                                                        | -                         | URL poster                        |
| 14  | backdrop_url        | VARCHAR(500)                                                        | -                         | URL backdrop                      |
| 15  | movie_type          | ENUM(SINGLE, SERIES)                                                | NOT NULL                  | Loại phim                         |
| 16  | total_episodes      | INT                                                                 | -                         | Tổng tập (SERIES)                 |
| 17  | status              | ENUM(DRAFT, PROCESSING, READY, PUBLISHED, FAILED, REJECTED, BANNED) | NOT NULL                  | Trạng thái video                  |
| 18  | visibility          | ENUM(PUBLIC, PRIVATE, MEMBERS_ONLY)                                 | NOT NULL                  | Phạm vi hiển thị                  |
| 19  | is_premium          | BOOLEAN                                                             | -                         | Phim premium                      |
| 20  | is_featured         | BOOLEAN                                                             | -                         | Phim nổi bật                      |
| 21  | rating              | DECIMAL(3,1)                                                        | -                         | Điểm đánh giá (0.0-10.0)          |
| 22  | view_count          | BIGINT                                                              | -                         | Số lượt xem                       |
| 23  | raw_file_key        | VARCHAR(500)                                                        | -                         | Key file gốc trên MinIO           |
| 24  | master_playlist_key | VARCHAR(500)                                                        | -                         | Key HLS master playlist           |
| 25  | resolutions         | JSON                                                                | -                         | Danh sách resolution đã transcode |
| 26  | created_by          | UUID (String)                                                       | FOREIGN KEY (user)        | Người tạo (admin)                 |
| 27  | deleted_at          | TIMESTAMP                                                           | -                         | Soft delete                       |
| 28  | created_at          | TIMESTAMP                                                           | DEFAULT CURRENT_TIMESTAMP | Ngày tạo                          |
| 29  | updated_at          | TIMESTAMP                                                           | -                         | Ngày cập nhật                     |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- UNIQUE: idx_movie_slug (slug)
- FOREIGN KEY: created_by → user(id)
- INDEX: idx_movie_movie_type, idx_movie_release_year, idx_movie_rating, idx_movie_status, idx_movie_deleted_at

**Many-to-Many:** movie ↔ genre (via movie_genre table)

> Slug auto-generate từ title: "Hoa Khai Cẩm Tú" → "hoa-khai-cam-tu"

---

### 4. movie_genre (Liên kết phim - thể loại)

| STT | Column   | Type          | Constraint          | Description |
| --- | -------- | ------------- | ------------------- | ----------- |
| 1   | movie_id | UUID (String) | FOREIGN KEY (movie) | ID phim     |
| 2   | genre_id | UUID (String) | FOREIGN KEY (genre) | ID thể loại |

**Index:**

- PRIMARY KEY: (movie_id, genre_id)
- FOREIGN KEY: movie_id → movie(id)
- FOREIGN KEY: genre_id → genre(id)

---

### 5. episode (Tập phim)

| STT | Column              | Type                                                                | Constraint                    | Description             |
| --- | ------------------- | ------------------------------------------------------------------- | ----------------------------- | ----------------------- |
| 1   | id                  | UUID (String)                                                       | PRIMARY KEY                   | ID tập                  |
| 2   | movie_id            | UUID (String)                                                       | FOREIGN KEY (movie), NOT NULL | ID phim (series)        |
| 3   | episode_number      | INT                                                                 | NOT NULL                      | Số tập                  |
| 4   | title               | VARCHAR(255)                                                        | -                             | Tên tập                 |
| 5   | description         | TEXT                                                                | -                             | Mô tả tập               |
| 6   | duration            | INT                                                                 | -                             | Thời lượng (phút)       |
| 7   | status              | ENUM(DRAFT, PROCESSING, READY, PUBLISHED, FAILED, REJECTED, BANNED) | NOT NULL                      | Trạng thái video        |
| 8   | raw_file_key        | VARCHAR(500)                                                        | -                             | Key file gốc trên MinIO |
| 9   | master_playlist_key | VARCHAR(500)                                                        | -                             | Key HLS master playlist |
| 10  | resolutions         | JSON                                                                | -                             | Danh sách resolution    |
| 11  | thumbnail_url       | VARCHAR(500)                                                        | -                             | URL thumbnail           |
| 12  | air_date            | DATE                                                                | -                             | Ngày phát hành          |
| 13  | is_premium          | BOOLEAN                                                             | -                             | Tập premium             |
| 14  | view_count          | BIGINT                                                              | -                             | Số lượt xem             |
| 15  | deleted_at          | TIMESTAMP                                                           | -                             | Soft delete             |
| 16  | created_at          | TIMESTAMP                                                           | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo                |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- FOREIGN KEY: movie_id → movie(id)
- UNIQUE: (movie_id, episode_number)
- INDEX: idx_episode_episode_number, idx_episode_status, idx_episode_deleted_at

---

### 6. upload_session (Phiên upload video)

| STT | Column          | Type          | Constraint                | Description                       |
| --- | --------------- | ------------- | ------------------------- | --------------------------------- |
| 1   | id              | UUID (String) | PRIMARY KEY               | ID phiên upload                   |
| 2   | target_id       | VARCHAR(36)   | NOT NULL                  | ID đối tượng (movie hoặc episode) |
| 3   | target_type     | VARCHAR(20)   | NOT NULL                  | Loại: MOVIE hoặc EPISODE          |
| 4   | raw_file_key    | VARCHAR(500)  | NOT NULL                  | Key file trên MinIO               |
| 5   | upload_id       | VARCHAR(512)  | NOT NULL                  | MinIO multipart upload ID         |
| 6   | file_size_bytes | BIGINT        | NOT NULL                  | Kích thước file (bytes)           |
| 7   | file_name       | VARCHAR(255)  | NOT NULL                  | Tên file gốc                      |
| 8   | status          | VARCHAR(20)   | NOT NULL                  | ACTIVE, COMPLETED, ABORTED        |
| 9   | expires_at      | TIMESTAMP     | NOT NULL                  | Hết hạn (created + 24h)           |
| 10  | created_at      | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP | Ngày tạo                          |

**Index:**

- PRIMARY KEY: id
- INDEX: idx_upload_session_target (target_id, target_type)
- INDEX: idx_upload_session_status_expires (status, expires_at)

> Upload session TTL: 24 giờ. Part size: 16MB. Presigned URL expiry: 900s.

---

### 7. upload_part (Part đã upload)

| STT | Column      | Type          | Constraint                   | Description            |
| --- | ----------- | ------------- | ---------------------------- | ---------------------- |
| 1   | id          | UUID (String) | PRIMARY KEY                  | ID part                |
| 2   | session_id  | UUID (String) | FOREIGN KEY (upload_session) | ID phiên upload        |
| 3   | part_number | INT           | NOT NULL                     | Số thứ tự part         |
| 4   | etag        | VARCHAR(255)  | NOT NULL                     | ETag từ MinIO response |
| 5   | size_bytes  | BIGINT        | NOT NULL                     | Kích thước part        |
| 6   | uploaded_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP    | Thời điểm upload       |

**Index:**

- PRIMARY KEY: id
- FOREIGN KEY: session_id → upload_session(id) ON DELETE CASCADE
- UNIQUE: (session_id, part_number)

---

### 8. comment (Bình luận)

| STT | Column      | Type          | Constraint                    | Description                      |
| --- | ----------- | ------------- | ----------------------------- | -------------------------------- |
| 1   | id          | UUID (String) | PRIMARY KEY                   | ID bình luận                     |
| 2   | user_id     | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người comment                 |
| 3   | movie_id    | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim                          |
| 4   | episode_id  | UUID (String) | FOREIGN KEY (episode)         | ID tập (nullable)                |
| 5   | content     | TEXT          | NOT NULL                      | Nội dung comment                 |
| 6   | parent_id   | UUID (String) | FOREIGN KEY (comment)         | Comment cha (self-ref, nullable) |
| 7   | is_approved | BOOLEAN       | -                             | Đã duyệt                         |
| 8   | like_count  | INT           | -                             | Số lượt like                     |
| 9   | deleted_at  | TIMESTAMP     | -                             | Soft delete                      |
| 10  | created_at  | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo                         |
| 11  | updated_at  | TIMESTAMP     | -                             | Ngày cập nhật                    |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- INDEX: idx_comment_user_id, idx_comment_movie_id, idx_comment_episode_id, idx_comment_deleted_at

**Relationships:**

- ManyToOne → user, movie, episode (nullable), comment (parent, self-ref)
- OneToMany → comment (replies, mappedBy "parent")

---

### 9. rating (Đánh giá)

| STT | Column     | Type          | Constraint                    | Description       |
| --- | ---------- | ------------- | ----------------------------- | ----------------- |
| 1   | id         | UUID (String) | PRIMARY KEY                   | ID đánh giá       |
| 2   | user_id    | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người đánh giá |
| 3   | movie_id   | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim           |
| 4   | score      | INT           | NOT NULL                      | Điểm              |
| 5   | review     | TEXT          | -                             | Nhận xét          |
| 6   | deleted_at | TIMESTAMP     | -                             | Soft delete       |
| 7   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo          |
| 8   | updated_at | TIMESTAMP     | -                             | Ngày cập nhật     |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- UNIQUE: (user_id, movie_id)
- INDEX: idx_rating_movie_id, idx_rating_score, idx_rating_deleted_at

---

### 10. list_favorite (Danh sách yêu thích)

| STT | Column     | Type          | Constraint                    | Description   |
| --- | ---------- | ------------- | ----------------------------- | ------------- |
| 1   | id         | UUID (String) | PRIMARY KEY                   | ID yêu thích  |
| 2   | user_id    | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người dùng |
| 3   | movie_id   | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim       |
| 4   | deleted_at | TIMESTAMP     | -                             | Soft delete   |
| 5   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày thêm     |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- UNIQUE: (user_id, movie_id)
- INDEX: idx_list_favorite_user_id, idx_list_favorite_deleted_at

---

### 11. history_watching (Lịch sử xem)

| STT | Column         | Type          | Constraint                    | Description           |
| --- | -------------- | ------------- | ----------------------------- | --------------------- |
| 1   | id             | UUID (String) | PRIMARY KEY                   | ID lịch sử            |
| 2   | user_id        | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người dùng         |
| 3   | movie_id       | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim               |
| 4   | episode_id     | UUID (String) | FOREIGN KEY (episode)         | ID tập (nullable)     |
| 5   | watch_duration | INT           | -                             | Thời gian xem (giây)  |
| 6   | total_duration | INT           | -                             | Tổng thời gian (giây) |
| 7   | progress       | DECIMAL(5,2)  | -                             | Tiến độ xem (%)       |
| 8   | is_completed   | BOOLEAN       | -                             | Đã xem xong           |
| 9   | last_position  | INT           | -                             | Vị trí xem cuối       |
| 10  | watched_at     | TIMESTAMP     | -                             | Ngày xem              |
| 11  | created_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo              |

**Index:**

- PRIMARY KEY: id
- INDEX: idx_history_watching_user_id, idx_history_watching_watched_at

---

### 12. membership_plan (Gói thành viên)

| STT | Column        | Type          | Constraint                | Description        |
| --- | ------------- | ------------- | ------------------------- | ------------------ |
| 1   | id            | UUID (String) | PRIMARY KEY               | ID gói             |
| 2   | name          | VARCHAR(100)  | NOT NULL, UNIQUE          | Tên gói            |
| 3   | slug          | VARCHAR(120)  | NOT NULL, UNIQUE          | Slug SEO-friendly  |
| 4   | price         | DECIMAL(10,2) | NOT NULL                  | Giá                |
| 5   | duration_days | INT           | NOT NULL                  | Số ngày            |
| 6   | max_devices   | INT           | -                         | Số thiết bị tối đa |
| 7   | can_download  | BOOLEAN       | -                         | Cho phép tải       |
| 8   | video_quality | VARCHAR(20)   | -                         | Chất lượng video   |
| 9   | description   | TEXT          | -                         | Mô tả gói          |
| 10  | is_active     | BOOLEAN       | -                         | Đang hoạt động     |
| 11  | created_at    | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP | Ngày tạo           |
| 12  | updated_at    | TIMESTAMP     | -                         | Ngày cập nhật      |

> Slug auto-generate từ name: "Gói Premium" → "goi-premium"

**Index:**

- PRIMARY KEY: id
- UNIQUE: name, slug

---

### 13. membership (Đăng ký thành viên)

| STT | Column             | Type                                  | Constraint                              | Description     |
| --- | ------------------ | ------------------------------------- | --------------------------------------- | --------------- |
| 1   | id                 | UUID (String)                         | PRIMARY KEY                             | ID đăng ký      |
| 2   | user_id            | UUID (String)                         | FOREIGN KEY (user), NOT NULL            | ID người dùng   |
| 3   | membership_plan_id | UUID (String)                         | FOREIGN KEY (membership_plan), NOT NULL | ID gói          |
| 4   | start_date         | DATE                                  | NOT NULL                                | Ngày bắt đầu    |
| 5   | end_date           | DATE                                  | NOT NULL                                | Ngày kết thúc   |
| 6   | payment_status     | ENUM(PENDING, PAID, FAILED, REFUNDED) | -                                       | Trạng thái TT   |
| 7   | auto_renewal       | BOOLEAN                               | -                                       | Tự động gia hạn |
| 8   | is_active          | BOOLEAN                               | -                                       | Đang hoạt động  |
| 9   | deleted_at         | TIMESTAMP                             | -                                       | Soft delete     |
| 10  | created_at         | TIMESTAMP                             | DEFAULT CURRENT_TIMESTAMP               | Ngày tạo        |
| 11  | updated_at         | TIMESTAMP                             | -                                       | Ngày cập nhật   |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- FOREIGN KEY: user_id → user(id), membership_plan_id → membership_plan(id)
- INDEX: idx_membership_user_id, idx_membership_end_date, idx_membership_deleted_at

---

### 14. payment_order (Đơn thanh toán)

| STT | Column                 | Type                                        | Constraint                              | Description                  |
| --- | ---------------------- | ------------------------------------------- | --------------------------------------- | ---------------------------- |
| 1   | id                     | UUID (String)                               | PRIMARY KEY                             | ID đơn                       |
| 2   | user_id                | UUID (String)                               | FOREIGN KEY (user), NOT NULL            | ID người dùng                |
| 3   | membership_plan_id     | UUID (String)                               | FOREIGN KEY (membership_plan), NOT NULL | ID gói                       |
| 4   | plan_name              | VARCHAR(100)                                | NOT NULL                                | Tên gói snapshot             |
| 5   | price                  | DECIMAL(10,2)                               | NOT NULL                                | Số tiền                      |
| 6   | duration_days          | INT                                         | NOT NULL                                | Số ngày gói                  |
| 7   | gateway                | VARCHAR(50)                                 | NOT NULL                                | Cổng TT (VNPAY, MOMO, PAYOS) |
| 8   | status                 | ENUM(PENDING, COMPLETED, FAILED, CANCELLED) | NOT NULL                                | Trạng thái                   |
| 9   | transaction_code       | VARCHAR(100)                                | NOT NULL, UNIQUE                        | Mã giao dịch                 |
| 10  | gateway_transaction_id | VARCHAR(255)                                | -                                       | Mã giao dịch từ gateway      |
| 11  | checkout_url           | VARCHAR(500)                                | -                                       | URL thanh toán               |
| 12  | failure_reason         | TEXT                                        | -                                       | Lý do thất bại               |
| 13  | metadata               | JSON                                        | -                                       | Metadata bổ sung             |
| 14  | requested_at           | TIMESTAMP                                   | -                                       | Thời điểm yêu cầu            |
| 15  | completed_at           | TIMESTAMP                                   | -                                       | Thời điểm hoàn tất           |
| 16  | failed_at              | TIMESTAMP                                   | -                                       | Thời điểm thất bại           |
| 17  | cancelled_at           | TIMESTAMP                                   | -                                       | Thời điểm hủy                |
| 18  | created_at             | TIMESTAMP                                   | DEFAULT CURRENT_TIMESTAMP               | Ngày tạo                     |
| 19  | updated_at             | TIMESTAMP                                   | -                                       | Ngày cập nhật                |

**Index:**

- PRIMARY KEY: id
- FOREIGN KEY: user_id → user(id), membership_plan_id → membership_plan(id)
- INDEX: idx_payment_order_user_id, idx_payment_order_status, idx_payment_order_transaction_code

---

### 15. refresh_token (Refresh Token Sessions)

| STT | Column     | Type          | Constraint                | Description          |
| --- | ---------- | ------------- | ------------------------- | -------------------- |
| 1   | id         | UUID (String) | PRIMARY KEY               | ID record            |
| 2   | user_id    | VARCHAR(255)  | NOT NULL                  | ID người dùng        |
| 3   | token_id   | VARCHAR(255)  | NOT NULL, UNIQUE          | ID token từ JWT      |
| 4   | is_admin   | BOOLEAN       | NOT NULL                  | Token admin hay user |
| 5   | expires_at | TIMESTAMP     | NOT NULL                  | Thời điểm hết hạn    |
| 6   | revoked_at | TIMESTAMP     | -                         | Ngày revoke (logout) |
| 7   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP | Ngày tạo             |

**Index:**

- PRIMARY KEY: id
- UNIQUE: idx_refresh_token_token_id (token_id)
- INDEX: idx_refresh_token_user_id (user_id)

> user_id là plain column, không có FK relation trong JPA entity.

---

### 16. otp_verification (OTP xác thực)

| STT | Column     | Type                               | Constraint                | Description       |
| --- | ---------- | ---------------------------------- | ------------------------- | ----------------- |
| 1   | id         | UUID (String)                      | PRIMARY KEY               | ID record         |
| 2   | email      | VARCHAR(255)                       | NOT NULL                  | Email             |
| 3   | code_hash  | VARCHAR(255)                       | NOT NULL                  | Hash của mã OTP   |
| 4   | type       | ENUM(REGISTRATION, PASSWORD_RESET) | NOT NULL                  | Loại OTP          |
| 5   | expires_at | TIMESTAMP                          | NOT NULL                  | Thời điểm hết hạn |
| 6   | used_at    | TIMESTAMP                          | -                         | Thời điểm sử dụng |
| 7   | created_at | TIMESTAMP                          | DEFAULT CURRENT_TIMESTAMP | Ngày tạo          |

**Index:**

- PRIMARY KEY: id
- INDEX: idx_otp_email (email)
- INDEX: idx_otp_expires_at (expires_at)

---

### 17. email_verification (Xác thực email bằng token)

| STT | Column     | Type          | Constraint                   | Description       |
| --- | ---------- | ------------- | ---------------------------- | ----------------- |
| 1   | id         | UUID (String) | PRIMARY KEY                  | ID record         |
| 2   | user_id    | UUID (String) | FOREIGN KEY (user), NOT NULL | ID người dùng     |
| 3   | token      | VARCHAR(255)  | NOT NULL, UNIQUE             | Token xác thực    |
| 4   | expires_at | TIMESTAMP     | NOT NULL                     | Thời điểm hết hạn |
| 5   | used_at    | TIMESTAMP     | -                            | Thời điểm sử dụng |
| 6   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP    | Ngày tạo          |

**Index:**

- PRIMARY KEY: id
- FOREIGN KEY: user_id → user(id)
- UNIQUE: token

---

### 18. notification (Thông báo)

| STT | Column         | Type                                                                                     | Constraint                   | Description               |
| --- | -------------- | ---------------------------------------------------------------------------------------- | ---------------------------- | ------------------------- |
| 1   | id             | UUID (String)                                                                            | PRIMARY KEY                  | ID thông báo              |
| 2   | user_id        | UUID (String)                                                                            | FOREIGN KEY (user), NOT NULL | ID người dùng             |
| 3   | type           | ENUM(NEW_REPORT, REPORT_RESOLVED, NEW_EPISODE, MEMBERSHIP_EXPIRY, COMMENT_REPLY, SYSTEM) | NOT NULL                     | Loại thông báo            |
| 4   | title          | VARCHAR(255)                                                                             | NOT NULL                     | Tiêu đề                   |
| 5   | content        | TEXT                                                                                     | -                            | Nội dung                  |
| 6   | reference_type | ENUM(REPORT, MOVIE, EPISODE, COMMENT, MEMBERSHIP)                                        | -                            | Loại đối tượng tham chiếu |
| 7   | reference_id   | VARCHAR(36)                                                                              | -                            | ID đối tượng tham chiếu   |
| 8   | is_read        | BOOLEAN                                                                                  | -                            | Đã đọc                    |
| 9   | read_at        | TIMESTAMP                                                                                | -                            | Thời điểm đọc             |
| 10  | deleted_at     | TIMESTAMP                                                                                | -                            | Soft delete               |
| 11  | created_at     | TIMESTAMP                                                                                | DEFAULT CURRENT_TIMESTAMP    | Ngày tạo                  |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- INDEX: idx_notification_user_id, idx_notification_type, idx_notification_created_at, idx_notification_deleted_at

---

### 19. report (Báo cáo vi phạm)

| STT | Column      | Type                                                        | Constraint                   | Description      |
| --- | ----------- | ----------------------------------------------------------- | ---------------------------- | ---------------- |
| 1   | id          | UUID (String)                                               | PRIMARY KEY                  | ID report        |
| 2   | reporter_id | UUID (String)                                               | FOREIGN KEY (user), NOT NULL | ID người báo cáo |
| 3   | target_type | ENUM(COMMENT, MOVIE, USER)                                  | NOT NULL                     | Loại đối tượng   |
| 4   | target_id   | VARCHAR(36)                                                 | NOT NULL                     | ID đối tượng     |
| 5   | reason      | ENUM(SPAM, INAPPROPRIATE, COPYRIGHT, BROKEN_CONTENT, OTHER) | NOT NULL                     | Lý do            |
| 6   | description | TEXT                                                        | -                            | Mô tả chi tiết   |
| 7   | status      | ENUM(PENDING, REVIEWING, RESOLVED, REJECTED)                | -                            | Trạng thái       |
| 8   | resolved_by | UUID (String)                                               | FOREIGN KEY (user)           | Admin xử lý      |
| 9   | admin_note  | TEXT                                                        | -                            | Ghi chú admin    |
| 10  | resolved_at | TIMESTAMP                                                   | -                            | Thời điểm xử lý  |
| 11  | deleted_at  | TIMESTAMP                                                   | -                            | Soft delete      |
| 12  | created_at  | TIMESTAMP                                                   | DEFAULT CURRENT_TIMESTAMP    | Ngày tạo         |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- INDEX: idx_report_reporter_id, idx_report_target_type, idx_report_status, idx_report_deleted_at

---

### 20. outbox_event (Outbox Pattern)

| STT | Column         | Type          | Constraint                | Description     |
| --- | -------------- | ------------- | ------------------------- | --------------- |
| 1   | id             | UUID (String) | PRIMARY KEY               | ID event        |
| 2   | aggregate_id   | VARCHAR(36)   | NOT NULL                  | ID aggregate    |
| 3   | aggregate_type | VARCHAR(50)   | NOT NULL                  | Loại aggregate  |
| 4   | event_type     | VARCHAR(100)  | NOT NULL                  | Loại event      |
| 5   | event_data     | JSON          | NOT NULL                  | Payload event   |
| 6   | processed      | BOOLEAN       | -                         | Đã xử lý        |
| 7   | processed_at   | TIMESTAMP     | -                         | Thời điểm xử lý |
| 8   | created_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP | Ngày tạo        |

**Index:**

- PRIMARY KEY: id
- INDEX: idx_outbox_event_aggregate_id, idx_outbox_event_event_type, idx_outbox_event_processed, idx_outbox_event_created_at

---

## Enums Summary

| Enum                      | Values                                                                             |
| ------------------------- | ---------------------------------------------------------------------------------- |
| UserRole                  | ADMIN, USER                                                                        |
| AccountStatus             | ACTIVE, INACTIVE                                                                   |
| GenreStatus               | ACTIVE, INACTIVE                                                                   |
| MovieType                 | SINGLE, SERIES                                                                     |
| VideoStatus               | DRAFT, PROCESSING, READY, PUBLISHED, FAILED, REJECTED, BANNED                      |
| VideoVisibility           | PUBLIC, PRIVATE, MEMBERS_ONLY                                                      |
| OtpType                   | REGISTRATION, PASSWORD_RESET                                                       |
| UploadType                | AVATAR, POSTER, VIDEO                                                              |
| MembershipPaymentStatus   | PENDING, PAID, FAILED, REFUNDED                                                    |
| PaymentOrderStatus        | PENDING, COMPLETED, FAILED, CANCELLED                                              |
| PaymentGateway            | VNPAY, MOMO, PAYOS                                                                 |
| PaymentStatus             | PENDING, COMPLETED, FAILED, REFUNDED                                               |
| SortOrder                 | asc, desc                                                                          |
| NotificationType          | NEW_REPORT, REPORT_RESOLVED, NEW_EPISODE, MEMBERSHIP_EXPIRY, COMMENT_REPLY, SYSTEM |
| NotificationReferenceType | REPORT, MOVIE, EPISODE, COMMENT, MEMBERSHIP                                        |
| ReportTargetType          | COMMENT, MOVIE, USER                                                               |
| ReportReason              | SPAM, INAPPROPRIATE, COPYRIGHT, BROKEN_CONTENT, OTHER                              |
| ReportStatus              | PENDING, REVIEWING, RESOLVED, REJECTED                                             |

---

## Data Flow

```
User (đăng ký) → OTP Verification (xác thực email)
  ↓
Membership Plan → Membership (đăng ký gói) → Payment Order (thanh toán)
  ↓
Admin tạo Movie (metadata)
  ↓
Upload Session → Upload Parts (video resumable upload)
  ↓
Movie + Genre (movie_genre join)
  ↓
Episode (nếu SERIES)
  ↓
User xem Movie/Episode
  ↓
History Watching (lưu lịch sử)
  ↓
Comment (bình luận) + Rating (đánh giá) + Favorite (yêu thích)
  ↓
Report (báo cáo vi phạm) → Notification (thông báo)
  ↓
Outbox Event (event publishing)
```

---

## Redis Cache (Optional)

```
# User session
user:cache:<user_id> → { userId, email, name, role }
TTL: 1 hour

# Movie list
movie:list:<page>:<filter> → { movies[], pagination }
TTL: 24 hours

# Movie detail
movie:detail:<movie_id> → { movieDetail, genres, episodes }
TTL: 24 hours
```

---

## Cập nhật lịch sử

### 2026-08-25 - v3.1

- Bảng `membership_plan`: thêm column `slug` (VARCHAR(120), NOT NULL, UNIQUE) — slug SEO-friendly auto-generate từ name
- Sửa enum AccountStatus: `ACTIVE, SUSPENDED, INACTIVE, BANNED` → `ACTIVE, INACTIVE` (khớp code thực tế)
- Sửa enum PaymentGateway: thêm `PAYOS`
- Thêm enum PaymentStatus, SortOrder
- Sửa `payment_order.gateway` description: thêm PAYOS
- Bảng `user`: thêm column `is_banned` (BOOLEAN, NOT NULL, DEFAULT false) và `last_active_at` (TIMESTAMP, nullable) — đã có trong JPA entity nhưng thiếu trong doc

### 2026-08-18 - v3.0

- Xóa bảng `role` (dùng ENUM UserRole trực tiếp, không có bảng riêng)
- Cập nhật bảng `user`: xóa role_id FK, birth_date, profile_picture, membership_status, membership_expiry_date, is_active, password_changed_at; thêm profile_url, role (enum), account_status (enum), failed_login_attempts, first_failure_at, locked_until, deleted_at
- Cập nhật enum MovieType: MOVIE/TV_SERIES → SINGLE/SERIES
- Cập nhật enum VideoStatus: DRAFT/COMPLETED/UPCOMING/DISCONTINUED → DRAFT/PROCESSING/READY/PUBLISHED/FAILED/REJECTED/BANNED
- Xóa bảng `transaction`, thay bằng `payment_order` (với gateway, checkout_url, failure_reason, metadata)
- Xóa bảng `advertisement` (chưa có entity)
- Thêm bảng `otp_verification` (OTP xác thực đăng ký/reset password)
- Thêm bảng `email_verification` (xác thực email bằng token)
- Thêm bảng `notification` (thông báo với type, reference)
- Thêm bảng `report` (báo cáo vi phạm comment/movie/user)
- Thêm bảng `outbox_event` (outbox pattern)
- Cập nhật bảng `comment`: xóa is_spoiler, đổi comment_id→id; thêm episode_id, parent_id (self-ref), is_approved, deleted_at
- Cập nhật bảng `rating`: đổi rating_id→id, score DECIMAL→INT; thêm review, deleted_at
- Cập nhật bảng `list_favorite`: đổi favorite_id→id, added_at→created_at; thêm deleted_at
- Cập nhật bảng `history_watching`: đổi history_id→id, watched_duration→watch_duration; thêm episode_id, progress, is_completed, last_position; xóa updated_at
- Cập nhật bảng `membership_plan`: đổi plan_id→id; xóa monthly_price/yearly_price/hd_quality/4k_quality/offline_download/ad_free; thêm price, duration_days, can_download, video_quality, is_active, updated_at
- Cập nhật bảng `membership`: đổi membership_id→id, plan_id→membership_plan_id, auto_renew→auto_renewal; thêm payment_status, deleted_at, updated_at
- Cập nhật bảng `refresh_token`: đổi tên từ refresh_session, token_id không còn là PK mà là UNIQUE column; thêm id (PK), is_admin; xóa refresh_token column
- Thêm Enums Summary section đầy đủ
- Tổng: 17 → 20 bảng

### 2026-08-17 - v2.0

- Cập nhật genre, movie, episode
- Thêm bảng movie_genre, upload_session, upload_part
- Tổng: 14 → 17 bảng

### 2026-07-21 - v1.0 (Initial)

- Created database.md with 14 tables
