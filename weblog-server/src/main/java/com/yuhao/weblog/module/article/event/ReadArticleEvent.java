package com.yuhao.weblog.module.article.event;

import org.springframework.context.ApplicationEvent;

/**
 * 文章阅读事件：供异步监听器累加阅读量与当日 PV
 */
public class ReadArticleEvent extends ApplicationEvent {

    private final Long articleId;

    public ReadArticleEvent(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }
}
