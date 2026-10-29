package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDetailVO {
    private Long id;
    private String title;
    private String cover;
    private String summary;
    private String content;
    private String contentHtml;
    private Long categoryId;
    private String categoryName;
    private List<Long> tagIds;
    private String tagNames;
    private Long readCount;
    /** 是否置顶 */
    private Integer isTop;
    private LocalDateTime createTime;
}
