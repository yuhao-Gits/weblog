package com.yuhao.weblog.module.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.weblog.module.comment.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
