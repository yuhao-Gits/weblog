package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

@Data
public class QueryTagReq {

    private Long current = 1L;
    private Long size = 10L;
    private String name;
}
