package com.yuhao.weblog.module.article.model.req;

import lombok.Data;

/**
 * 通用分页请求（仅页码 / 每页条数）
 */
@Data
public class BasePageReq {

    private Long current = 1L;
    private Long size = 10L;
}
