package com.yuhao.weblog.module.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客设置表（全局唯一一条）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_blog_settings")
public class BlogSettings extends BaseDO {

    @TableId(type = IdType.INPUT)
    private Long id = 1L;

    /** 博客名称 */
    private String blogName;

    /** 作者名 */
    private String authorName;

    /** 作者简介 */
    private String introduction;

    /** 作者头像 */
    private String avatar;

    /** 博客 Logo */
    private String logo;

    /** GitHub 主页 */
    private String githubHome;

    /** Gitee 主页 */
    private String giteeHome;

    /** 微信号 / 二维码等扩展字段 */
    private String wechatQrCode;

    /** QQ 号 */
    private String qqQrCode;

    /** 敏感词（逗号分隔） */
    private String sensitiveWords;

    /** 评论审核开关：1-开启 0-关闭 */
    private Integer commentAudit;

    /** 邮件通知配置（评论通知博主） */
    private String emailHost;
    private Integer emailPort;
    private String emailUsername;
    private String emailPassword;
    private String emailFrom;
}
