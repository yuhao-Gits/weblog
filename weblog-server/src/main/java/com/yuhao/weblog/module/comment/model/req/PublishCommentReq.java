package com.yuhao.weblog.module.comment.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发布评论请求
 */
@Data
public class PublishCommentReq {
    /** 文章 ID */
    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

    /** 父评论 ID（回复时传，顶层为 0 或不传） */
    private Long parentId;

    /** 被回复人昵称 */
    private String replyUserName;

    /** 评论人昵称 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** 邮箱 */
    private String email;

    /** QQ 号 */
    private String qq;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
