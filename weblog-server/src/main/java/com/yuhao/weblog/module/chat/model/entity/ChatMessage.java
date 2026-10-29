package com.yuhao.weblog.module.chat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhao.weblog.module.admin.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天室消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_chat_message")
public class ChatMessage extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String avatar;

    /** 消息内容 */
    private String content;

    /** 消息类型：1-用户消息 2-系统通知 */
    private Integer type;
}
