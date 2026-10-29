package com.yuhao.weblog.module.chat.model.req;

import lombok.Data;

/**
 * 聊天消息请求（客户端 -> 服务端）
 */
@Data
public class ChatMessageReq {
    /** 消息类型：1-用户消息 */
    private Integer type;
    /** 消息内容 */
    private String content;
    /** 昵称 */
    private String nickname;
    /** 头像 */
    private String avatar;
}
