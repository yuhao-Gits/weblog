package com.yuhao.weblog.module.comment.model.req;

import lombok.Data;

/**
 * 评论分页查询请求
 */
@Data
public class QueryCommentReq {
    private Integer current = 1;
    private Integer size = 10;
    /** 文章 ID（前台某篇文章评论） */
    private Long articleId;
    /** 审核状态（后台筛选） */
    private Integer status;
}
