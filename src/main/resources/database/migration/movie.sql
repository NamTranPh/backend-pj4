
-- Role
CREATE TABLE IF NOT EXISTS role (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User
CREATE TABLE IF NOT EXISTS user (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    profile_picture VARCHAR(500),
    phone VARCHAR(20) NOT NULL UNIQUE,,
    address TEXT,
    role_id INT DEFAULT 1,
    membership_status ENUM('free', 'premium', 'vip') DEFAULT 'free',
    membership_expiry_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(role_id)
);

-- Genre
CREATE TABLE IF NOT EXISTS genre (
    genre_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Movie
CREATE TABLE IF NOT EXISTS movie (
    movie_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    original_title VARCHAR(255),
    description TEXT,
    release_year YEAR,
    duration INT COMMENT 'Thời lượng tính bằng phút',
    director VARCHAR(255),
    actors TEXT,
    country VARCHAR(100),
    language VARCHAR(50),
    trailer_url VARCHAR(500),
    poster_url VARCHAR(500),
    backdrop_url VARCHAR(500),
    rating DECIMAL(3,1) DEFAULT 0.0,
    view_count INT DEFAULT 0,
    movie_type ENUM('single', 'series') NOT NULL COMMENT 'single: phim lẻ, series: phim bộ',
    total_episodes INT DEFAULT 1 COMMENT 'Tổng số tập (phim bộ)',
    status ENUM('coming_soon', 'ongoing', 'completed') DEFAULT 'completed',
    is_premium BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(user_id),
    INDEX idx_movie_type (movie_type),
    INDEX idx_release_year (release_year),
    INDEX idx_rating (rating)
);

-- Episode
CREATE TABLE IF NOT EXISTS episode (
    episode_id INT PRIMARY KEY AUTO_INCREMENT,
    movie_id INT NOT NULL,
    episode_number INT NOT NULL,
    title VARCHAR(255),
    description TEXT,
    duration INT COMMENT 'Thời lượng tập phim',
    video_url VARCHAR(500),
    thumbnail_url VARCHAR(500),
    air_date DATE,
    is_premium BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    UNIQUE KEY unique_movie_episode (movie_id, episode_number),
    INDEX idx_episode_number (episode_number)
);

-- movie_genre
CREATE TABLE IF NOT EXISTS movie_genre (
    movie_id INT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
);

-- comment
CREATE TABLE IF NOT EXISTS comment (
    comment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    episode_id INT NULL,
    content TEXT NOT NULL,
    parent_id INT NULL,
    is_approved BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (episode_id) REFERENCES episode(episode_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comment(comment_id) ON DELETE CASCADE,
    INDEX idx_movie_comment (movie_id),
    INDEX idx_episode_comment (episode_id),
    INDEX idx_user_comment (user_id)
);

-- rating
CREATE TABLE IF NOT EXISTS rating (
    rating_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    score INT NOT NULL,
    review TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_movie_rating (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    INDEX idx_movie_rating (movie_id),
    INDEX idx_score (score)
);

-- list_favorite
CREATE TABLE IF NOT EXISTS list_favorite (
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_movie_favorite (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    INDEX idx_user_favorite (user_id)
);

-- history_watching
CREATE TABLE IF NOT EXISTS history_watching (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    episode_id INT NULL,
    watch_duration INT DEFAULT 0,
    total_duration INT,
    progress DECIMAL(5,2) DEFAULT 0.00,
    is_completed BOOLEAN DEFAULT FALSE,
    watched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_position INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movie(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (episode_id) REFERENCES episode(episode_id) ON DELETE CASCADE,
    INDEX idx_user_history (user_id),
    INDEX idx_watched_at (watched_at)
);

-- membership_plan
CREATE TABLE IF NOT EXISTS membership_plan (
    plan_id INT PRIMARY KEY AUTO_INCREMENT,
    plan_name VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    duration_days INT NOT NULL,
    max_devices INT DEFAULT 1,
    can_download BOOLEAN DEFAULT FALSE,
    video_quality VARCHAR(20) DEFAULT 'HD',
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- membership
CREATE TABLE IF NOT EXISTS membership (
    member_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    plan_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    payment_status ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending',
    auto_renewal BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id) REFERENCES membership_plan(plan_id),
    INDEX idx_user_membership (user_id),
    INDEX idx_end_date (end_date)
);

-- transaction
CREATE TABLE IF NOT EXISTS transaction (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    membership_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('credit_card', 'paypal', 'bank_transfer', 'momo', 'vnpay') NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed', 'refunded') DEFAULT 'pending',
    transaction_code VARCHAR(100) UNIQUE,
    gateway_transaction_id VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (membership_id) REFERENCES membership(member_id),
    INDEX idx_user_transaction (user_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_payment_status (payment_status)
);

-- advertisement
CREATE TABLE IF NOT EXISTS advertisement (
    ad_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    image_url VARCHAR(500),
    click_url VARCHAR(500),
    ad_type ENUM('banner', 'popup', 'video', 'sidebar') DEFAULT 'banner',
    position VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    click_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(user_id),
    INDEX idx_date_range (start_date, end_date),
    INDEX idx_ad_type (ad_type)
);
