-- ============================================================
-- Weblog 初始数据（MySQL 8）
-- 说明：INSERT IGNORE + 固定主键，脚本幂等，重复启动不覆盖已有数据
-- 用户账号、访问统计由 InitDataRunner 在应用启动时按需生成
-- ============================================================

INSERT IGNORE INTO t_blog_settings (id, blog_name, author_name, introduction, avatar, logo, github_home, gitee_home, wechat_qr_code, qq_qr_code)
VALUES (1, 'Weblog', 'Weblog', '一个基于 Spring Boot + Vue 3 的前后端分离博客', '', '', 'https://github.com', 'https://gitee.com', '', '');

INSERT IGNORE INTO t_category (id, name) VALUES (1, 'Java');
INSERT IGNORE INTO t_category (id, name) VALUES (2, 'Spring Boot');
INSERT IGNORE INTO t_category (id, name) VALUES (3, 'Vue');

INSERT IGNORE INTO t_tag (id, name) VALUES (1, 'Java');
INSERT IGNORE INTO t_tag (id, name) VALUES (2, 'Spring Boot');
INSERT IGNORE INTO t_tag (id, name) VALUES (3, 'Vue 3');
INSERT IGNORE INTO t_tag (id, name) VALUES (4, 'MySQL');

INSERT IGNORE INTO t_article (id, title, cover, summary, content, content_html, category_id, read_count, status, is_top)
VALUES (1, '欢迎使用 Weblog', '', '这是 Weblog 的示例文章',
        '# 欢迎使用 Weblog\n\n这是 Weblog 的示例文章，删除或编辑它吧。\n\n```java\nSystem.out.println(\"Hello Weblog\");\n```\n',
        '<h1>欢迎使用 Weblog</h1><p>这是 Weblog 的示例文章，删除或编辑它吧。</p>',
        1, 0, 1, 0);

INSERT IGNORE INTO t_article_tag_rel (id, article_id, tag_id) VALUES (1, 1, 1);
INSERT IGNORE INTO t_article_tag_rel (id, article_id, tag_id) VALUES (2, 1, 2);

-- 知识库初始目录与示例文章
INSERT IGNORE INTO t_knowledge_directory (id, title, parent_id, sort) VALUES (1, 'Java 基础', 0, 1);
INSERT IGNORE INTO t_knowledge_directory (id, title, parent_id, sort) VALUES (2, 'Spring Boot', 0, 2);
INSERT IGNORE INTO t_knowledge_directory (id, title, parent_id, sort) VALUES (3, '前端', 0, 3);

INSERT IGNORE INTO t_knowledge_article (id, title, content, content_html, summary, directory_id, is_top, status)
VALUES (1, 'Java 入门', '# Java 入门\n\n欢迎来到 Java 基础知识库。\n', '<h1>Java 入门</h1><p>欢迎来到 Java 基础知识库。</p>', 'Java 入门示例文章', 1, 1, 1);
