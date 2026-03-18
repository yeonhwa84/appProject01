-- ────────────────────────────────────────────────────
-- 미연시 게임 DDL (MySQL)
-- project01 DB에 실행하세요
-- ────────────────────────────────────────────────────

-- 1. 유저 테이블 (게스트 + 이름 로그인 통합)
CREATE TABLE IF NOT EXISTS vn_user (
    user_id      VARCHAR(36)  NOT NULL COMMENT '유저 ID (UUID 또는 게스트ID)',
    user_type    VARCHAR(10)  NOT NULL COMMENT 'GUEST | NAMED',
    user_name    VARCHAR(50)           COMMENT '이름 (NAMED 타입만 사용)',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게임 유저';

-- 2. 세이브 데이터 테이블
CREATE TABLE IF NOT EXISTS vn_save (
    save_id       BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       VARCHAR(36)  NOT NULL COMMENT '유저 ID (vn_user.user_id)',
    scene_id      VARCHAR(100) NOT NULL COMMENT '현재 씬 ID',
    affection     JSON                  COMMENT '{"하나":65,"유이":50,"세리":55}',
    current_bg    VARCHAR(100)          COMMENT '현재 배경 파일명',
    saved_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (save_id),
    UNIQUE KEY uq_user_save (user_id),
    CONSTRAINT fk_save_user FOREIGN KEY (user_id) REFERENCES vn_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게임 세이브 데이터';

-- 인덱스
CREATE INDEX IF NOT EXISTS idx_vn_user_name ON vn_user(user_name);
