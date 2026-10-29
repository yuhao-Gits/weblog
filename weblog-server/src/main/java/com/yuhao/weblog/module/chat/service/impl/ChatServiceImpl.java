package com.yuhao.weblog.module.chat.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.chat.mapper.ChatMessageMapper;
import com.yuhao.weblog.module.chat.model.entity.ChatMessage;
import com.yuhao.weblog.module.chat.model.vo.ChatMessageVO;
import com.yuhao.weblog.module.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 聊天室服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageMapper chatMessageMapper;

    /** 在线用户：sessionId -> 昵称 */
    private final ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public ChatMessage saveMessage(String nickname, String avatar, String content, Integer type) {
        ChatMessage message = new ChatMessage();
        message.setNickname(nickname);
        message.setAvatar(avatar);
        message.setContent(content);
        message.setType(type == null ? 1 : type);
        chatMessageMapper.insert(message);
        return message;
    }

    @Override
    public Result<PageResult<ChatMessageVO>> pageHistory(Long lastMessageId, Integer size) {
        long pageSize = size == null ? 20 : size;
        // 游标分页：id < lastMessageId（雪花 ID 递增），取最近 pageSize 条
        Page<ChatMessage> page = chatMessageMapper.selectPage(
                new Page<>(1, pageSize),
                Wrappers.<ChatMessage>lambdaQuery()
                        .lt(lastMessageId != null, ChatMessage::getId, lastMessageId)
                        .orderByDesc(ChatMessage::getId));
        // 倒序查出来后反转，保证返回按时间升序
        List<ChatMessage> records = new ArrayList<>(page.getRecords());
        java.util.Collections.reverse(records);
        List<ChatMessageVO> vos = records.stream().map(this::toVO).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    public List<String> onlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    /** 用户上线 */
    public void addOnline(String sessionId, String nickname) {
        onlineUsers.put(sessionId, nickname);
    }

    /** 用户下线 */
    public void removeOnline(String sessionId) {
        onlineUsers.remove(sessionId);
    }

    private ChatMessageVO toVO(ChatMessage m) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(String.valueOf(m.getId()));
        vo.setNickname(m.getNickname());
        vo.setAvatar(m.getAvatar());
        vo.setContent(m.getContent());
        vo.setType(m.getType());
        vo.setCreateTime(m.getCreateTime());
        return vo;
    }
}
