-- Tạo database
CREATE DATABASE fast_caculate_game

USE fast_caculate_game;

-- Bảng người dùng
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) UNIQUE NOT NULL,
    email         VARCHAR(100),
    password_hash VARCHAR(255),
    elo_rating    INT DEFAULT 1000,   -- điểm xếp hạng theo ELO
    total_score   INT DEFAULT 0,      -- tổng điểm cộng dồn
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng trận đấu
CREATE TABLE matches (
    match_id    INT AUTO_INCREMENT PRIMARY KEY,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status      ENUM('waiting', 'playing', 'finished') DEFAULT 'waiting',
    winner_id   INT NULL,
);

-- Bảng câu hỏi trong trận
CREATE TABLE match_questions (
    match_question_id INT AUTO_INCREMENT PRIMARY KEY,
    match_id     INT NOT NULL,
    target_value INT NOT NULL,             -- số cần đạt
    numbers      JSON NOT NULL,            -- mảng số cho sẵn
    allowed_ops  VARCHAR(20) DEFAULT '+-*/',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(match_id)
);

-- Bảng lịch sử đấu
CREATE TABLE match_history (
    history_id   INT AUTO_INCREMENT PRIMARY KEY,
    match_id     INT NOT NULL,
    user_id      INT NOT NULL,
    score        INT DEFAULT 0,           -- điểm trong trận
    elo_change   INT,
    time_taken   INT,                     -- thời gian trả lời (ms hoặc giây)
    is_winner    BOOLEAN DEFAULT FALSE,
    finished_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(match_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);