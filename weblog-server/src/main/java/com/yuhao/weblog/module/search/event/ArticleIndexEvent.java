package com.yuhao.weblog.module.search.event;

import com.yuhao.weblog.module.admin.model.entity.Article;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 文章索引同步事件：新增/更新/删除
 */
@Getter
public class ArticleIndexEvent extends ApplicationEvent {

    /** 操作类型：UPDATE（新增/更新）、DELETE */
    private final String operation;
    /** 文章实体（DELETE 时可空） */
    private final Article article;
    /** 文章 ID（DELETE 时使用） */
    private final Long articleId;

    public ArticleIndexEvent(Object source, String operation, Article article, Long articleId) {
        super(source);
        this.operation = operation;
        this.article = article;
        this.articleId = articleId;
    }

    public static ArticleIndexEvent update(Object source, Article article) {
        return new ArticleIndexEvent(source, "UPDATE", article, null);
    }

    public static ArticleIndexEvent delete(Object source, Long articleId) {
        return new ArticleIndexEvent(source, "DELETE", null, articleId);
    }
}
