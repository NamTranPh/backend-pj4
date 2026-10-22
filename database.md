# DATABASE.md - Schema & Relationships

> **Mục đích:** Tài liệu database - schema, index, relation, data flow

**Cập nhật lần cuối:** 2026-10-11  
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

| STT | Bảng                 | Mục đích                                  | Rows dự kiến |
| --- | -------------------- | ----------------------------------------- | ------------ |
| 1   | `user`               | Người dùng                                | ~10k         |
| 2   | `genre`              | Thể loại phim                             | ~100         |
| 3   | `movie`              | Thông tin phim                            | ~1k          |
| 4   | `movie_genre`        | Liên kết phim - thể loại                  | ~5k          |
| 5   | `episode`            | Tập phim (series)                         | ~5k          |
| 6   | `upload_session`     | Phiên upload video resumable              | ~100         |
| 7   | `upload_part`        | Part đã upload trong phiên                | ~5k          |
| 8   | `comment`            | Bình luận                                 | ~100k        |
| 9   | `rating`             | Đánh giá                                  | ~50k         |
| 10  | `list_favorite`      | Danh sách yêu thích                       | ~100k        |
| 11  | `history_watching`   | Lịch sử xem                               | ~500k        |
| 12  | `membership_plan`    | Gói thành viên                            | ~5           |
| 13  | `membership`         | Đăng ký thành viên                        | ~5k          |
| 14  | `payment_order`      | Đơn thanh toán                            | ~5k          |
| 15  | `refresh_token`      | Refresh token session                     | ~10k         |
| 16  | `otp_verification`   | OTP xác thực                              | ~10k         |
| 17  | `email_verification` | ⛔ **CHƯA DÙNG** — xem ghi chú ở mục 17   | **0**        |
| 18  | `notification`       | Thông báo                                 | ~50k         |
| 19  | `report`             | ⏳ **CHƯA DÙNG** — module chưa làm        | **0**        |
| 20  | `outbox_event`       | ⏳ **CHƯA DÙNG** — chưa có message broker | **0**        |
| 21  | `comment_like`       | Lượt thích bình luận                      | ~300k        |

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
| 13b | token_version         | INT                    | NOT NULL, DEFAULT 0       | Tăng mỗi lần đổi mật khẩu |
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

| STT | Column      | Type          | Constraint                    | Description                                |
| --- | ----------- | ------------- | ----------------------------- | ------------------------------------------ |
| 1   | id          | UUID (String) | PRIMARY KEY                   | ID bình luận                               |
| 2   | user_id     | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người comment                           |
| 3   | movie_id    | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim                                    |
| 4   | episode_id  | UUID (String) | FOREIGN KEY (episode)         | ID tập (nullable)                          |
| 5   | content     | TEXT          | NOT NULL                      | Nội dung comment                           |
| 6   | parent_id   | UUID (String) | FOREIGN KEY (comment)         | Comment cha trực tiếp (self-ref, nullable) |
| 7   | root_id     | UUID (String) | INDEX                         | Comment gốc của cả luồng (nullable)        |
| 8   | is_approved | BOOLEAN       | -                             | Đã duyệt                                   |
| 9   | like_count  | INT           | -                             | Số lượt like (ảnh chụp lúc gộp)            |
| 10  | deleted_at  | TIMESTAMP     | -                             | Soft delete                                |
| 11  | created_at  | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo                                   |
| 12  | updated_at  | TIMESTAMP     | -                             | Ngày cập nhật                              |

**Soft delete:** `@Where(clause = "deleted_at IS NULL")`

**Index:**

- PRIMARY KEY: id
- INDEX: idx_comment_user_id, idx_comment_movie_id, idx_comment_episode_id, idx_comment_deleted_at, idx_comment_root_id
- INDEX GHÉP: idx_comment_movie_created (movie_id, created_at), idx_comment_movie_like (movie_id, like_count)

Hai index ghép phục vụ đúng hai lựa chọn sắp xếp của `GET /api/v1/movies/{slug}/comments` (`createdAt` và `likeCount`). Chỉ có index riêng trên `movie_id` thì mệnh đề `WHERE` dùng được index nhưng `ORDER BY` vẫn phải filesort toàn bộ bình luận của phim rồi mới `LIMIT`.

`idx_comment_movie_id` giờ **thừa** (tiền tố trái của `idx_comment_movie_created` đã bao nó). Giữ lại vì `ddl-auto=update` không DROP index; xoá tay nếu muốn:

```sql
DROP INDEX idx_comment_movie_id ON comment;
```

**⚠️ `parent_id` và `root_id` là hai thứ khác nhau, đừng nhầm:**

| Cột         | Nghĩa                                              | Dùng để                            |
| ----------- | -------------------------------------------------- | ---------------------------------- |
| `parent_id` | cha **trực tiếp** — đang trả lời ai                | ghép cây lúc hiển thị              |
| `root_id`   | bình luận **gốc** của cả luồng, bất kể sâu mấy cấp | nạp hết cả luồng bằng MỘT truy vấn |

Bình luận gốc có `root_id = NULL`; mọi con cháu đều trỏ thẳng về gốc, kể cả cấp 5, cấp 10.

Lý do có cột thứ hai: danh sách phân trang theo bình luận **gốc**, rồi phải nạp toàn bộ con cháu của trang đó. Chỉ có `parent_id` thì phải dò từng cấp — mỗi cấp một truy vấn và không biết trước khi nào dừng. Có `root_id` thì `WHERE root_id IN (các gốc)` là xong, sâu bao nhiêu cũng **2 truy vấn**.

**⚠️ `like_count` là ảnh chụp, không phải số thời gian thực.** Nguồn sự thật là bảng `comment_like`. Lượt thích được đếm ở Redis rồi job gộp xuống cột này mỗi 5 phút bằng `COUNT(*)` (đếm lại chứ không cộng dồn, nên Redis restart cũng tự khớp lại). Số hiển thị cho client = cột này + phần chênh lệch đang ở Redis.

**Relationships:**

- ManyToOne → user, movie, episode (nullable), comment (parent, self-ref)
- OneToMany → comment (replies, mappedBy "parent")

---

### 9. rating (Đánh giá)

| STT | Column     | Type          | Constraint                    | Description                   |
| --- | ---------- | ------------- | ----------------------------- | ----------------------------- |
| 1   | id         | UUID (String) | PRIMARY KEY                   | ID đánh giá                   |
| 2   | user_id    | UUID (String) | FOREIGN KEY (user), NOT NULL  | ID người đánh giá             |
| 3   | movie_id   | UUID (String) | FOREIGN KEY (movie), NOT NULL | ID phim                       |
| 4   | score      | INT           | NOT NULL                      | Điểm                          |
| 5   | review     | TEXT          | -                             | Nhận xét                      |
| 6   | deleted_at | TIMESTAMP     | -                             | ⛔ **CỘT CHẾT** — xem ghi chú |
| 7   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP     | Ngày tạo                      |
| 8   | updated_at | TIMESTAMP     | -                             | Ngày cập nhật                 |

`score` là **số nguyên 1–5** (số sao). Ràng buộc nằm ở tầng ứng dụng (`RateMovieService` + `@Min/@Max` ở DTO), không có `CHECK` ở database.

**Index:**

- PRIMARY KEY: id
- UNIQUE: (user_id, movie_id)
- INDEX: idx_rating_movie_id, idx_rating_score, idx_rating_deleted_at

**⛔ Bảng này XOÁ CỨNG, cột `deleted_at` không bao giờ được ghi.**

Entity vẫn mang `@Where(deleted_at IS NULL)` từ thiết kế ban đầu, nhưng **không được xoá mềm**, vì nó xung khắc với `UNIQUE(user_id, movie_id)`:

```
1. A chấm phim X 5 sao        -> dòng (A, X, 5), deleted_at = NULL
2. A xoá (nếu xoá mềm)        -> dòng (A, X, 5), deleted_at = now   <- DÒNG VẪN CÒN
3. A chấm lại phim X 3 sao
   findByUserIdAndMovieId     -> @Where thêm "AND deleted_at IS NULL" -> KHÔNG thấy dòng cũ
                              -> code tưởng chưa từng chấm           -> INSERT dòng mới
   MySQL                      -> Duplicate entry for key uk_rating_user_movie   ✗
```

Kết quả nếu làm sai: **người dùng không bao giờ chấm lại được phim đã xoá đánh giá**. Bảng `list_favorite` có đúng cặp ràng buộc này và cũng xoá cứng, vì cùng lý do.

Xoá cứng an toàn ở đây vì một đánh giá chỉ là một con số và một đoạn nhận xét của chính người đó — không ai trả lời nó, không có nhánh con, không nghiệp vụ nào cần lịch sử. Khác hẳn `comment`, vốn có con cháu nên vẫn xoá mềm.

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

| STT | Column       | Type          | Constraint                | Description                               |
| --- | ------------ | ------------- | ------------------------- | ----------------------------------------- |
| 1   | id           | UUID (String) | PRIMARY KEY               | ID record                                 |
| 2   | user_id      | VARCHAR(255)  | NOT NULL                  | ID người dùng                             |
| 3   | token_id     | VARCHAR(255)  | NOT NULL, UNIQUE          | ID token từ JWT, đổi mỗi lần rotation     |
| 4   | session_id   | VARCHAR(36)   | -                         | ID phiên, GIỮ NGUYÊN qua mọi lần rotation |
| 5   | is_admin     | BOOLEAN       | NOT NULL                  | Token kênh admin hay kênh user            |
| 6   | expires_at   | TIMESTAMP     | NOT NULL                  | Thời điểm hết hạn                         |
| 7   | revoked_at   | TIMESTAMP     | -                         | Ngày revoke (logout / rotation / ban)     |
| 8   | user_agent   | VARCHAR(512)  | -                         | Trình duyệt, thiết bị lúc đăng nhập       |
| 9   | ip_address   | VARCHAR(45)   | -                         | IP client (45 ký tự đủ cho IPv6)          |
| 10  | last_used_at | TIMESTAMP     | -                         | Lần refresh gần nhất của phiên            |
| 11  | created_at   | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP | Ngày tạo                                  |

**Index:**

- PRIMARY KEY: id
- UNIQUE: idx_refresh_token_token_id (token_id)
- INDEX: idx_refresh_token_user_id (user_id)
- INDEX: idx_refresh_token_session_id (session_id)

> **Phân biệt `token_id` và `session_id`:** `token_id` định danh một tờ vé cụ thể, đổi mỗi
> 15 phút do rotation. `session_id` định danh một phiên đăng nhập (một thiết bị), cố định từ
> lúc login tới lúc logout. Thu hồi một token dùng `token_id`; "Đăng xuất thiết bị này" dùng
> `session_id`.

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

### 17. email_verification ⛔ CHƯA DÙNG

> **Bảng này không có và sẽ không có dòng nào.** Code Java của nó đã bị xoá ngày 2026-10-11.
> Bảng vẫn còn trong MySQL vì `ddl-auto=update` không bao giờ DROP bảng — xoá thủ công nếu muốn:
> `DROP TABLE email_verification;`
>
> **Vì sao bỏ:** đây là phương án _token gửi qua link_ cho việc xác thực email. Phương án thực tế
> được chọn là _OTP 6 số_, nằm ở bảng `otp_verification` (mục 16). Cả hai bảng được thêm cùng ngày
> 2026-08-18 (changelog v3.0) nhưng code chỉ đi theo nhánh OTP.
>
> Bản OTP thắng vì: cột `type` cho một bảng phục vụ cả đăng ký lẫn quên mật khẩu; lưu `code_hash`
> thay vì token nguyên văn (DB rò rỉ thì hash vô dụng); và dùng `email` dạng chuỗi thay vì khoá
> ngoại `user_id`, để luồng quên mật khẩu trả về giống hệt nhau dù email có tồn tại hay không
> (chống dò tài khoản).
>
> Hệ thống cũng **không có kênh gửi link** — cổng gửi mail duy nhất là
> `OtpSender.send(email, otpCode, type)`, chỉ gửi mã.

Cấu trúc bảng (giữ lại để tham khảo nếu sau này cần làm lại):

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

### 19. report ⏳ CHƯA DÙNG

> Đủ 9 file persistence (domain model, port, entity, mapper, adapter, JPA repository, 3 enum)
> nhưng **không có use case hay controller nào**. Bảng không có dòng nào.
> Giữ lại làm khung cho module báo cáo vi phạm sẽ làm sau.

### 19b. report (Báo cáo vi phạm) — cấu trúc

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

### 20. outbox_event ⏳ CHƯA DÙNG

> Đủ 6 file persistence nhưng **không có publisher hay job relay nào**. Bảng không có dòng nào.
>
> Outbox Pattern giải bài toán: _ghi database_ và _gửi message ra ngoài_ không thể nằm chung một
> transaction. Gửi trước khi commit thì rollback để lại message ma; commit xong mới gửi thì
> process chết là mất message. Cách giải: ghi event thành một dòng **trong cùng transaction**, rồi
> một job riêng quét các dòng `processed = false` để gửi và đánh dấu đã xử lý.
>
> pj4 hiện **không có message broker** (không Kafka, không RabbitMQ), mọi thứ chạy trong cùng một
> process nên chưa có gì để gửi ra ngoài. Giữ lại làm khung cho sau này.

### 20b. outbox_event (Outbox Pattern) — cấu trúc

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

### 21. comment_like (Lượt thích bình luận)

| STT | Column     | Type          | Constraint                      | Description     |
| --- | ---------- | ------------- | ------------------------------- | --------------- |
| 1   | id         | UUID (String) | PRIMARY KEY                     | ID lượt thích   |
| 2   | user_id    | UUID (String) | FOREIGN KEY (user), NOT NULL    | Người thích     |
| 3   | comment_id | UUID (String) | FOREIGN KEY (comment), NOT NULL | Bình luận       |
| 4   | created_at | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP       | Thời điểm thích |

**Không có `deleted_at`, không có `@Where`** — bỏ thích là **xoá hẳn dòng**. Xoá mềm ở đây sẽ đụng `UNIQUE(user_id, comment_id)` ngay lần thích lại, đúng cái bẫy mà bảng `rating` phải tránh.

**Index:**

- PRIMARY KEY: id
- UNIQUE: uk_comment_like_user_comment (user_id, comment_id)
- INDEX: idx_comment_like_comment_id, idx_comment_like_user_id

**Vì sao cần bảng riêng khi `comment.like_count` đã là một con số:**

| Câu hỏi                         | Chỉ có `like_count` | Có `comment_like`          |
| ------------------------------- | ------------------- | -------------------------- |
| Bấm thích 2 lần thì sao?        | +2, spam được       | đụng UNIQUE → 409          |
| Tải lại trang, tim đỏ hay rỗng? | không biết          | `WHERE user_id = ?` là ra  |
| Bỏ thích thì trừ của ai?        | chỉ biết −1         | xoá đúng dòng của người đó |

Đây là **nguồn sự thật**; `comment.like_count` chỉ là ảnh chụp để trang danh sách khỏi phải `COUNT(*)` cho từng dòng.

**Relationships:** ManyToOne → user, comment

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

## Cập nhật lịch sử

### 2026-10-14 - v3.6 - Comment lồng nhau + Rating 1–5 sao

**Hai thay đổi schema, cả hai đều là THÊM MỚI nên `ddl-auto=update` xử lý được:**

```sql
ALTER TABLE comment ADD COLUMN root_id VARCHAR(255) NULL;
CREATE INDEX idx_comment_root_id      ON comment (root_id);
CREATE INDEX idx_comment_movie_created ON comment (movie_id, created_at);
CREATE INDEX idx_comment_movie_like    ON comment (movie_id, like_count);

CREATE TABLE comment_like (
    id         VARCHAR(255) PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    comment_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_comment_like_user_comment UNIQUE (user_id, comment_id),
    CONSTRAINT fk_comment_like_user    FOREIGN KEY (user_id)    REFERENCES user (id),
    CONSTRAINT fk_comment_like_comment FOREIGN KEY (comment_id) REFERENCES comment (id)
);
CREATE INDEX idx_comment_like_comment_id ON comment_like (comment_id);
CREATE INDEX idx_comment_like_user_id    ON comment_like (user_id);
```

**Thay đổi ngữ nghĩa, không đổi cấu trúc:**

- `comment.like_count` giờ là **ảnh chụp lúc gộp**, không còn là số thời gian thực. Nguồn sự thật chuyển sang bảng `comment_like`.
- `rating.deleted_at` thành **cột chết** — bảng `rating` chuyển sang **xoá cứng** vì `@Where(deleted_at IS NULL)` xung khắc với `UNIQUE(user_id, movie_id)`. Xem ghi chú ở mục 9.
- `rating.score` chốt thang **1–5** (số sao), ràng buộc ở tầng ứng dụng.

**Dữ liệu cũ:** bảng `comment` và `rating` đang rỗng nên không cần backfill. Nếu sau này có dữ liệu `comment` cũ với `root_id = NULL` ở các dòng con, chúng sẽ không hiện ra trong cây — cần backfill bằng tay.

### 2026-10-11 - v3.5

**Không thay đổi schema.** Chỉ gỡ code và ghi chú lại tài liệu.

- Xoá toàn bộ code Java của `email_verification` (6 file). **Bảng vẫn còn trong MySQL** —
  `ddl-auto=update` không DROP bảng. Xoá thủ công nếu muốn:

```sql
DROP TABLE email_verification;
```

- Đánh dấu 3 bảng **chưa dùng** ở danh sách và ở mục chi tiết: `email_verification` (mục 17),
  `report` (mục 19), `outbox_event` (mục 20). Trước đây tài liệu mô tả cả 3 như đang hoạt động,
  còn ước lượng `email_verification` ~10k dòng và `outbox_event` ~10k dòng — thực tế cả 3 đều 0 dòng.

- `movie.genres` thêm `@BatchSize(size = 50)`: chỉ là chỉ dẫn fetch của Hibernate, **không đụng DDL**.

### 2026-10-02 - v3.4

Bảng `user`: thêm column `token_version`.

```sql
ALTER TABLE user ADD COLUMN token_version INT NOT NULL DEFAULT 0;
```

| Column          | Kiểu                   | Mô tả                                                                    |
| --------------- | ---------------------- | ------------------------------------------------------------------------ |
| `token_version` | INT NOT NULL DEFAULT 0 | Tăng lên mỗi khi mật khẩu thay đổi (`change-password`, `reset-password`) |

**Vì sao cần:** đổi mật khẩu trước đây chỉ thu hồi refresh token, còn access token cũ vẫn chạy tới khi hết hạn — kẻ tấn công chiếm được access token còn nguyên 15 phút sau khi nạn nhân đã đổi mật khẩu. Access token nay mang claim `ver`; `JwtAuthenticationFilter` đối chiếu với `token_version` hiện tại, lệch là từ chối ngay.

**Không breaking:** token phát trước bản này không có claim `ver` → đọc ra 0, khớp với người dùng chưa từng đổi mật khẩu.

> **Lưu ý triển khai:** dev dùng `ddl-auto=update` nên Hibernate tự sinh cột. Môi trường dùng chung chạy câu `ALTER TABLE` ở trên bằng tay trước khi deploy.

### 2026-09-21 - v3.3

Bảng `refresh_token`: thêm 4 column + 1 index, chuẩn bị cho tính năng Quản lý phiên đăng nhập (liệt kê thiết bị đang đăng nhập, đăng xuất từ xa). Chưa có API/UI.

```sql
ALTER TABLE refresh_token
  ADD COLUMN session_id   VARCHAR(36)  NULL,
  ADD COLUMN user_agent   VARCHAR(512) NULL,
  ADD COLUMN ip_address   VARCHAR(45)  NULL,
  ADD COLUMN last_used_at TIMESTAMP    NULL,
  ADD INDEX idx_refresh_token_session_id (session_id);
```

| Column         | Kiểu         | Null | Mô tả                                                                                                                                                                                                                                                                                     |
| -------------- | ------------ | ---- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `session_id`   | VARCHAR(36)  | ✓    | **Cố định xuyên suốt một phiên đăng nhập.** Rotation sinh `token_id` mới mỗi 15 phút và thu hồi cái cũ, nên sau một ngày một phiên đã đẻ ~96 `token_id` rời rạc. Không có `session_id` thì không nối chúng lại thành "một thiết bị" được, và nút "Đăng xuất thiết bị này" không cài được. |
| `user_agent`   | VARCHAR(512) | ✓    | Header `User-Agent` lúc đăng nhập/refresh, cắt bớt nếu dài hơn 512.                                                                                                                                                                                                                       |
| `ip_address`   | VARCHAR(45)  | ✓    | Ưu tiên phần tử đầu của `X-Forwarded-For`, fallback `getRemoteAddr()`. Dài 45 để chứa được IPv6. **Chỉ dùng để hiển thị**, không dùng làm căn cứ phân quyền vì client tự đặt header được.                                                                                                 |
| `last_used_at` | TIMESTAMP    | ✓    | Cập nhật mỗi lần rotation, để hiển thị "hoạt động 2 phút trước".                                                                                                                                                                                                                          |

Quan hệ giữa hai cột định danh: một `session_id` có nhiều `token_id` theo thời gian (rotation sinh `token_id` mới mỗi 15 phút, thu hồi cái cũ), còn `session_id` giữ nguyên từ lúc login tới lúc logout.

### 2026-09-07 - v3.2

- **Không có thay đổi schema.** Ghi chú lại thay đổi ngữ nghĩa của 3 column đã tồn tại trong bảng `user`:
  - `locked_until` — trước đây **chỉ được đọc, không nơi nào ghi** (lớp fallback chống brute-force là code chết). Từ bản này được ghi khi số lần đăng nhập sai vượt `auth.login.max-failures` trong cửa sổ `auth.login.failure-window-minutes`, giá trị = `now + auth.login.lock-minutes`.
  - `failed_login_attempts`, `first_failure_at` — reset về `0`/`NULL` khi khoá tài khoản hoặc khi đăng nhập thành công; bộ đếm bắt đầu lại từ 1 khi `first_failure_at` đã ra ngoài cửa sổ đếm.
  - Áp dụng cho **cả** luồng user (`/api/v1/auth/login`) và luồng admin (`/api/v1/admin/auth/login`) — trước đây admin chỉ đếm trên Redis.
- Số lần thử sai OTP được đếm trên **Redis** (key `{prefix}:otp-attempt:{type}:{email}`, TTL = `auth.otp.ttl-minutes`), **không thêm column** vào bảng `otp_verification`.

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
