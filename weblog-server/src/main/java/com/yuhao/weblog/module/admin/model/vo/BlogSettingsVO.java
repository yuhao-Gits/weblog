package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogSettingsVO {
    private String blogName;
    private String authorName;
    private String introduction;
    private String avatar;
    private String logo;
    private String githubHome;
    private String giteeHome;
    private String wechatQrCode;
    private String qqQrCode;
    private String sensitiveWords;
    private Integer commentAudit;
    private String emailHost;
    private Integer emailPort;
    private String emailUsername;
    private String emailFrom;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
