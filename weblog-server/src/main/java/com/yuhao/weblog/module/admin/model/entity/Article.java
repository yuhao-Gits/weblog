package com.yuhao.weblog.module.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_article")
public class Article extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 标题 */
    private String title;

    /** 封面图 URL */
    private String cover;

    /** 摘要 */
    private String summary;

    /** Markdown 正文 */
    private String content;

    /** HTML 正文（发布时由 Markdown 转换） */
    private String contentHtml;

    /** 所属分类 ID */
    private Long categoryId;

    /** 阅读量 */
    private Long readCount;

    /** 是否置顶：1-是 0-否 */
    private Integer isTop;

    /** 状态：1-已发布 0-草稿（本实现默认已发布） */
    private Integer status;
}
