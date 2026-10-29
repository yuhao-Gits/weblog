package com.yuhao.weblog.module.comment.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.comment.model.req.PublishCommentReq;
import com.yuhao.weblog.module.comment.model.vo.CommentVO;
import com.yuhao.weblog.module.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前台评论接口
 */
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class FrontCommentController {

    private final CommentService commentService;

    /** 发布评论 */
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody @Valid PublishCommentReq req) {
        return commentService.publish(req);
    }

    /** 某篇文章的评论列表（树形，仅已审核） */
    @GetMapping("/list")
    public Result<List<CommentVO>> list(@RequestParam("articleId") Long articleId) {
        return commentService.listByArticle(articleId);
    }
}
