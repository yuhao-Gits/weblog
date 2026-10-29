package com.yuhao.weblog.module.comment.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhao.weblog.module.admin.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
public class Comment extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 文章 ID */
    private Long articleId;

    /** 父评论 ID（0 表示顶层） */
    private Long parentId;

    /** 被回复人昵称 */
    private String replyUserName;

    /** 评论人昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** QQ 号 */
    private String qq;

    /** 头像 */
    private String avatar;

    /** 评论内容 */
    private String content;

    /** 审核状态：1-已通过 0-待审核 */
    private Integer status;
}
