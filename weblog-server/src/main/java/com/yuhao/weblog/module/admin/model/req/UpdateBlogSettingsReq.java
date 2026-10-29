package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

@Data
public class UpdateBlogSettingsReq {

    private String blogName;
    private String authorName;
    private String introduction;
    private String avatar;
    private String logo;
    private String githubHome;
    private String giteeHome;
    private String wechatQrCode;
    private String qqQrCode;
    /** 敏感词（逗号分隔） */
    private String sensitiveWords;
    /** 评论审核开关 */
    private Integer commentAudit;
    /** 邮件通知配置 */
    private String emailHost;
    private Integer emailPort;
    private String emailUsername;
    private String emailPassword;
    private String emailFrom;
}
