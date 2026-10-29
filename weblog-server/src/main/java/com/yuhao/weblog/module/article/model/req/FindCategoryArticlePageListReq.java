package com.yuhao.weblog.module.article.model.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 前台：某分类下文章分页请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindCategoryArticlePageListReq extends BasePageReq {

    /** 分类名称（URL 传参） */
    private String categoryName;
}
