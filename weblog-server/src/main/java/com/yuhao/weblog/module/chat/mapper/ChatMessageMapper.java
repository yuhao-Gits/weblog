package com.yuhao.weblog.module.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.weblog.module.chat.model.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
