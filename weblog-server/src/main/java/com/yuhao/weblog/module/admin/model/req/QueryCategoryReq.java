package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

@Data
public class QueryCategoryReq {

    /** 当前页 */
    private Long current = 1L;
    /** 每页条数 */
    private Long size = 10L;
    /** 分类名称（模糊） */
    private String name;
}
