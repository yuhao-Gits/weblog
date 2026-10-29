package com.yuhao.weblog.module.article.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FindArticleDetailRspVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private String contentHtml;
    private Long readCount;
    private LocalDateTime createTime;
    private String categoryName;
    private String tagNames;
}
