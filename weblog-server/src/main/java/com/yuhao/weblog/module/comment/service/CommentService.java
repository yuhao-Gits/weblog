package com.yuhao.weblog.module.comment.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.comment.model.req.PublishCommentReq;
import com.yuhao.weblog.module.comment.model.req.QueryCommentReq;
import com.yuhao.weblog.module.comment.model.vo.CommentVO;

import java.util.List;

/**
 * 评论服务
 */
public interface CommentService {

    /** 发布评论 */
    Result<?> publish(PublishCommentReq req);

    /** 前台：某文章评论列表（树形，仅已审核） */
    Result<List<CommentVO>> listByArticle(Long articleId);

    /** 后台：评论分页列表 */
    Result<PageResult<CommentVO>> pageList(QueryCommentReq req);

    /** 删除评论（含子评论） */
    Result<?> delete(Long commentId);

    /** 审核评论 */
    Result<?> audit(Long commentId, Integer status);
}
