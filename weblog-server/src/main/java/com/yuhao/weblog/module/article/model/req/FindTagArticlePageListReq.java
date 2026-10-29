package com.yuhao.weblog.module.article.model.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 前台：某标签下文章分页请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindTagArticlePageListReq extends BasePageReq {

    /** 标签名称（URL 传参） */
    private String tagName;
}
