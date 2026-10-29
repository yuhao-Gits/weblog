package com.yuhao.weblog.module.comment.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.comment.model.req.QueryCommentReq;
import com.yuhao.weblog.module.comment.model.vo.CommentVO;
import com.yuhao.weblog.module.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台评论管理接口
 */
@RestController
@RequestMapping("/admin/comment")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    /** 评论分页列表 */
    @PostMapping("/list")
    public Result<PageResult<CommentVO>> list(@RequestBody QueryCommentReq req) {
        return commentService.pageList(req);
    }

    /** 删除评论 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("commentId") Long commentId) {
        return commentService.delete(commentId);
    }

    /** 审核评论 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/audit")
    public Result<?> audit(@RequestParam("commentId") Long commentId,
                           @RequestParam("status") Integer status) {
        return commentService.audit(commentId, status);
    }
}
