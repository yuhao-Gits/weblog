package com.yuhao.weblog.module.knowledge.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库文章 VO
 */
@Data
public class ArticleVO {
    /** 文章 ID */
    private String id;
    /** 文章标题 */
    private String title;
    /** Markdown 正文 */
    private String content;
    /** 渲染后的 HTML 正文 */
    private String contentHtml;
    /** 摘要 */
    private String summary;
    /** 所属目录 ID */
    private String directoryId;
    /** 是否置顶 */
    private Integer isTop;
    /** 发布状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    /** 上一篇 ID */
    private String prevId;
    /** 下一篇 ID */
    private String nextId;
}
