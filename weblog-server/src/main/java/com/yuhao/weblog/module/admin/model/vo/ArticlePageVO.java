package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticlePageVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private Long categoryId;
    private String categoryName;
    private String tagNames;
    private Long readCount;
    /** 是否置顶 */
    private Integer isTop;
    private LocalDateTime createTime;
}
