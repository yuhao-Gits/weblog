package com.yuhao.weblog.module.knowledge.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识库目录 VO（树形结构）
 */
@Data
public class DirectoryVO {
    /** 目录 ID */
    private String id;
    /** 目录标题 */
    private String title;
    /** 父目录 ID */
    private String parentId;
    /** 排序权重 */
    private Integer sort;
    /** 该目录下文章数 */
    private Integer articleCount;
    /** 子目录 */
    private List<DirectoryVO> children;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
