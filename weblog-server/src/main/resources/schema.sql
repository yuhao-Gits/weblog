-- ============================================================
-- Weblog 表结构（MySQL 8，utf8mb4）
-- 说明：使用 IF NOT EXISTS + 幂等种子数据，重启不丢数据
-- ============================================================

CREATE TABLE IF NOT EXISTS t_user
(
    id           BIGINT       NOT NULL,
    username     VARCHAR(64)  NOT NULL,
    password     VARCHAR(128) NOT NULL,
    role         VARCHAR(32)  NOT NULL DEFAULT 'ROLE_ADMIN',
    create_time  DATETIME     NULL,
    update_time  DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_category
(
    id          BIGINT      NOT NULL,
    name        VARCHAR(64) NOT NULL,
    create_time DATETIME    NULL,
    update_time DATETIME    NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_tag
(
    id          BIGINT      NOT NULL,
    name        VARCHAR(64) NOT NULL,
    create_time DATETIME    NULL,
    update_time DATETIME    NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_article
(
    id           BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    cover        VARCHAR(512) NULL,
    summary      VARCHAR(1024) NULL,
    content      LONGTEXT     NULL,
    content_html LONGTEXT     NULL,
    category_id  BIGINT       NULL,
    read_count   BIGINT       NULL DEFAULT 0,
    status       INT          NULL DEFAULT 1,
    is_top       TINYINT      NULL DEFAULT 0,
    create_time  DATETIME     NULL,
    update_time  DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_article_category (category_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_article_tag_rel
(
    id         BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_rel_article (article_id),
    KEY idx_rel_tag (tag_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_blog_settings
(
    id             BIGINT       NOT NULL,
    blog_name      VARCHAR(128) NULL,
    author_name    VARCHAR(64)  NULL,
    introduction   VARCHAR(512) NULL,
    avatar         VARCHAR(512) NULL,
    logo           VARCHAR(512) NULL,
    github_home    VARCHAR(512) NULL,
    gitee_home     VARCHAR(512) NULL,
    wechat_qr_code VARCHAR(512) NULL,
    qq_qr_code     VARCHAR(512) NULL,
    sensitive_words VARCHAR(2048) NULL,
    comment_audit  TINYINT      NULL DEFAULT 0,
    email_host     VARCHAR(128) NULL,
    email_port     INT          NULL,
    email_username VARCHAR(128) NULL,
    email_password VARCHAR(128) NULL,
    email_from     VARCHAR(128) NULL,
    create_time    DATETIME     NULL,
    update_time    DATETIME     NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_statistics
(
    date          DATE   NOT NULL,
    pv            BIGINT NULL DEFAULT 0,
    uv            BIGINT NULL DEFAULT 0,
    article_count BIGINT NULL DEFAULT 0,
    PRIMARY KEY (date)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_comment
(
    id              BIGINT       NOT NULL,
    article_id      BIGINT       NOT NULL,
    parent_id       BIGINT       NULL DEFAULT 0,
    reply_user_name VARCHAR(64)  NULL,
    nickname        VARCHAR(64)  NOT NULL,
    email           VARCHAR(128) NULL,
    qq              VARCHAR(32)  NULL,
    avatar          VARCHAR(512) NULL,
    content         TEXT         NOT NULL,
    status          TINYINT      NULL DEFAULT 1,
    create_time     DATETIME     NULL,
    update_time     DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_comment_article (article_id),
    KEY idx_comment_parent (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ============================================================
-- 知识库（2.0）
-- ============================================================

CREATE TABLE IF NOT EXISTS t_knowledge_directory
(
    id          BIGINT      NOT NULL,
    title       VARCHAR(128) NOT NULL,
    parent_id   BIGINT      NULL DEFAULT 0,
    sort        INT         NULL DEFAULT 0,
    create_time DATETIME    NULL,
    update_time DATETIME    NULL,
    PRIMARY KEY (id),
    KEY idx_kdir_parent (parent_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS t_knowledge_article
(
    id           BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    content      LONGTEXT     NULL,
    content_html LONGTEXT     NULL,
    summary      VARCHAR(1024) NULL,
    directory_id BIGINT       NOT NULL,
    is_top       TINYINT      NULL DEFAULT 0,
    status       INT          NULL DEFAULT 0,
    create_time  DATETIME     NULL,
    update_time  DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_kart_directory (directory_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ============================================================
-- 聊天室消息（2.0）
-- ============================================================

CREATE TABLE IF NOT EXISTS t_chat_message
(
    id          BIGINT       NOT NULL,
    nickname    VARCHAR(64)  NOT NULL,
    avatar      VARCHAR(512) NULL,
    content     TEXT         NOT NULL,
    type        INT          NULL DEFAULT 1,
    create_time DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_chat_time (create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
