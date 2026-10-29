package com.yuhao.weblog.module.chat.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.chat.model.vo.ChatMessageVO;
import com.yuhao.weblog.module.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 聊天室接口（历史消息 + 在线用户）
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /** 历史消息（游标分页） */
    @GetMapping("/history")
    public Result<PageResult<ChatMessageVO>> history(@RequestParam(value = "lastMessageId", required = false) Long lastMessageId,
                                                     @RequestParam(value = "size", required = false) Integer size) {
        return chatService.pageHistory(lastMessageId, size);
    }

    /** 在线用户列表 */
    @GetMapping("/online")
    public Result<List<String>> online() {
        return Result.success(chatService.onlineUsers());
    }
}
