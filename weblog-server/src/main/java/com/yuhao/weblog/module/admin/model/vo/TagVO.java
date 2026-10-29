package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TagVO {
    private Long id;
    private String name;
    private LocalDateTime createTime;

    /** 该标签下文章数 */
    private Long articlesTotal;
}
