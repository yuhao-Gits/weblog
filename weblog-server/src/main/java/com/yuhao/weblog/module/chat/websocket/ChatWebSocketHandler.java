package com.yuhao.weblog.module.chat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuhao.weblog.module.chat.model.entity.ChatMessage;
import com.yuhao.weblog.module.chat.service.impl.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天室 WebSocket 处理器
 *
 * 消息协议（JSON）：
 * 客户端 -> 服务端：
 *   { "type": "join", "nickname": "xxx", "avatar": "xxx" }  加入聊天室
 *   { "type": "message", "content": "xxx" }                  发送消息
 *   { "type": "ping" }                                        心跳
 * 服务端 -> 客户端：
 *   { "type": "system", "content": "xxx" }                   系统通知
 *   { "type": "message", ... }                               广播用户消息
 *   { "type": "online", "users": ["a","b"] }                 在线用户列表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ChatServiceImpl chatService;
    private final ObjectMapper objectMapper;

    /** sessionId -> session */
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    /** sessionId -> 昵称 */
    private final ConcurrentHashMap<String, String> nicknameMap = new ConcurrentHashMap<>();
    /** sessionId -> 头像 */
    private final ConcurrentHashMap<String, String> avatarMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(payload, Map.class);
        } catch (Exception e) {
            return;
        }
        String type = data.get("type") == null ? "" : String.valueOf(data.get("type"));
        String sessionId = session.getId();

        switch (type) {
            case "join": {
                String nickname = data.get("nickname") == null ? "游客" : String.valueOf(data.get("nickname"));
                String avatar = data.get("avatar") == null ? "" : String.valueOf(data.get("avatar"));
                nicknameMap.put(sessionId, nickname);
                avatarMap.put(sessionId, avatar);
                chatService.addOnline(sessionId, nickname);
                broadcastSystem(nickname + " 加入了聊天室");
                broadcastOnlineList();
                break;
            }
            case "message": {
                String nickname = nicknameMap.getOrDefault(sessionId, "游客");
                String avatar = avatarMap.getOrDefault(sessionId, "");
                String content = data.get("content") == null ? "" : String.valueOf(data.get("content"));
                if (content.trim().isEmpty()) {
                    return;
                }
                ChatMessage saved = chatService.saveMessage(nickname, avatar, content, 1);
                Map<String, Object> msg = new HashMap<>();
                msg.put("type", "message");
                msg.put("id", String.valueOf(saved.getId()));
                msg.put("nickname", nickname);
                msg.put("avatar", avatar);
                msg.put("content", content);
                msg.put("createTime", saved.getCreateTime() == null
                        ? LocalDateTime.now().format(FMT) : saved.getCreateTime().format(FMT));
                broadcast(msg);
                break;
            }
            case "ping":
                // 心跳，忽略
                break;
            default:
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        sessions.remove(sessionId);
        String nickname = nicknameMap.remove(sessionId);
        avatarMap.remove(sessionId);
        if (nickname != null) {
            chatService.removeOnline(sessionId);
            broadcastSystem(nickname + " 离开了聊天室");
            broadcastOnlineList();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try {
            afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
        } finally {
            session.close();
        }
    }

    private void broadcastSystem(String content) throws Exception {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "system");
        msg.put("content", content);
        broadcast(msg);
    }

    private void broadcastOnlineList() throws Exception {
        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "online");
        msg.put("users", chatService.onlineUsers());
        broadcast(msg);
    }

    private void broadcast(Map<String, Object> msg) throws Exception {
        String json = objectMapper.writeValueAsString(msg);
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (Exception e) {
                    log.warn("广播消息失败: {}", e.getMessage());
                }
            }
        }
    }
}
