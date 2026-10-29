package com.yuhao.weblog.module.chat.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.chat.model.entity.ChatMessage;
import com.yuhao.weblog.module.chat.model.vo.ChatMessageVO;

import java.util.List;

/**
 * 聊天室服务
 */
public interface ChatService {

    /** 保存消息（WebSocket 收到后调用） */
    ChatMessage saveMessage(String nickname, String avatar, String content, Integer type);

    /** 分页查询历史消息（游标分页：返回 <= messageId 的最近 N 条） */
    Result<PageResult<ChatMessageVO>> pageHistory(Long lastMessageId, Integer size);

    /** 当前在线用户昵称列表 */
    List<String> onlineUsers();
}
