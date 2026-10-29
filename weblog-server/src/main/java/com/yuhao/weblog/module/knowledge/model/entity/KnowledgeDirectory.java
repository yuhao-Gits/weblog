package com.yuhao.weblog.module.knowledge.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yuhao.weblog.module.admin.model.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库目录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_knowledge_directory")
public class KnowledgeDirectory extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 目录标题 */
    private String title;

    /** 父目录 ID（0 表示一级目录） */
    private Long parentId;

    /** 排序权重（越小越靠前） */
    private Integer sort;
}
