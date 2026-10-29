package com.yuhao.weblog.module.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文章-标签 关联表（多对多）
 */
@Data
@TableName("t_article_tag_rel")
public class ArticleTagRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long articleId;
    private Long tagId;
}
