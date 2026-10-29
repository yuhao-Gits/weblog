package com.yuhao.weblog.module.article.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章归档：按年份分组
 */
@Data
public class FindArchiveArticlePageListRspVO {

    private List<ArchiveGroup> archives;

    @Data
    public static class ArchiveGroup {
        private Integer year;
        private List<ArchiveItem> articles;
    }

    @Data
    public static class ArchiveItem {
        private Long id;
        private String title;
        private LocalDateTime createTime;
    }
}
