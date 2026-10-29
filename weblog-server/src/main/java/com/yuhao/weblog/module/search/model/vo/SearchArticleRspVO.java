package com.yuhao.weblog.module.search.model.vo;

import lombok.Data;

/**
 * 站内搜索结果
 */
@Data
public class SearchArticleRspVO {
    /** 文章 ID */
    private String id;
    /** 标题 */
    private String title;
    /** 高亮片段（含 <em> 标签） */
    private String highlight;
}
