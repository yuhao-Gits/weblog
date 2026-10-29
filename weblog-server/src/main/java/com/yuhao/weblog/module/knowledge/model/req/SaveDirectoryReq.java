package com.yuhao.weblog.module.knowledge.model.req;

import lombok.Data;

/**
 * 知识库目录保存请求（新增/编辑共用）
 */
@Data
public class SaveDirectoryReq {
    /** 目录 ID（编辑时传） */
    private Long id;
    /** 目录标题 */
    private String title;
    /** 父目录 ID（0 表示一级目录） */
    private Long parentId;
    /** 排序权重 */
    private Integer sort;
}
