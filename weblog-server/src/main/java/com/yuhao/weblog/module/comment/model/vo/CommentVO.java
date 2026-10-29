package com.yuhao.weblog.module.comment.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论展示 VO（树形结构）
 */
@Data
public class CommentVO {
    /** 评论 ID */
    private String id;
    /** 文章 ID */
    private String articleId;
    /** 父评论 ID */
    private String parentId;
    /** 评论人昵称 */
    private String nickname;
    /** 被回复人昵称 */
    private String replyUserName;
    /** 头像 */
    private String avatar;
    /** 评论内容 */
    private String content;
    /** 审核状态 */
    private Integer status;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 子评论 */
    private List<CommentVO> children;
}
