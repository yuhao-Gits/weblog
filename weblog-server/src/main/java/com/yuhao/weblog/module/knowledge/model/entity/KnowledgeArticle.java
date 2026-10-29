package com.yuhao.weblog.module.knowledge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhao.weblog.module.admin.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文章
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_knowledge_article")
public class KnowledgeArticle extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 文章标题 */
    private String title;

    /** Markdown 正文 */
    private String content;

    /** 渲染后的 HTML 正文 */
    private String contentHtml;

    /** 摘要 */
    private String summary;

    /** 所属目录 ID */
    private Long directoryId;

    /** 是否置顶：1-是 0-否 */
    private Integer isTop;

    /** 发布状态：1-已发布 0-未发布 */
    private Integer status;
}
