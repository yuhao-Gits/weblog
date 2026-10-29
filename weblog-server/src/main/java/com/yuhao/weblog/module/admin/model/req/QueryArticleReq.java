package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QueryArticleReq {

    private Long current = 1L;
    private Long size = 10L;
    /** 标题（模糊） */
    private String title;
    /** 分类 ID */
    private Long categoryId;
    /** 状态：1-已发布 0-草稿 */
    private Integer status;
    /** 起始时间 */
    private LocalDateTime startDate;
    /** 结束时间 */
    private LocalDateTime endDate;
}
