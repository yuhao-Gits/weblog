package com.yuhao.weblog.module.knowledge.model.req;

import lombok.Data;

/**
 * 知识库文章分页查询请求
 */
@Data
public class QueryArticleReq {
    private Integer current = 1;
    private Integer size = 10;
    /** 目录 ID（可选筛选） */
    private Long directoryId;
    /** 标题关键词 */
    private String title;
    /** 发布状态 */
    private Integer status;
}
