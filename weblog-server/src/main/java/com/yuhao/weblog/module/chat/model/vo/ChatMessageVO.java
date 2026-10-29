package com.yuhao.weblog.module.chat.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天消息 VO
 */
@Data
public class ChatMessageVO {
    /** 消息 ID */
    private String id;
    /** 昵称 */
    private String nickname;
    /** 头像 */
    private String avatar;
    /** 消息内容 */
    private String content;
    /** 消息类型：1-用户消息 2-系统通知 */
    private Integer type;
    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
