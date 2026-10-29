package com.yuhao.weblog.module.article.model.req;

import lombok.Data;

@Data
public class FindIndexArticlePageListReq {

    private Long current = 1L;
    private Long size = 10L;

    /** 分类 ID（可选） */
    private Long categoryId;

    /** 标签 ID（可选） */
    private Long tagId;
}
