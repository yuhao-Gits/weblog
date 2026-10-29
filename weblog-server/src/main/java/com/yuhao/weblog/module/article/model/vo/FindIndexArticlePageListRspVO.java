package com.yuhao.weblog.module.article.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FindIndexArticlePageListRspVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private Long readCount;
    private LocalDateTime createTime;
    private String categoryName;
    private String tagNames;
    /** 是否置顶 */
    private Integer isTop;
}
