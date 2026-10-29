package com.yuhao.weblog.module.knowledge.model.req;

import lombok.Data;

/**
 * 知识库文章保存请求（新增/编辑共用）
 */
@Data
public class SaveArticleReq {
    /** 文章 ID（编辑时传） */
    private Long id;
    /** 文章标题 */
    private String title;
    /** Markdown 正文 */
    private String content;
    /** 摘要 */
    private String summary;
    /** 所属目录 ID */
    private Long directoryId;
    /** 是否置顶：1-是 0-否 */
    private Integer isTop;
    /** 发布状态：1-已发布 0-未发布 */
    private Integer status;
}
