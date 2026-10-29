package com.yuhao.weblog.module.knowledge.model.req;

import lombok.Data;

/**
 * 知识库目录排序请求（拖拽排序）
 */
@Data
public class SortDirectoryReq {
    /** 目录 ID */
    private Long id;
    /** 新排序权重 */
    private Integer sort;
    /** 新父目录 ID */
    private Long parentId;
}
