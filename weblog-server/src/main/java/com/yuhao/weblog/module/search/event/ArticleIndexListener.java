package com.yuhao.weblog.module.search.event;

import com.yuhao.weblog.common.utils.LuceneHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * 文章索引同步监听器：文章新增/更新/删除后异步同步 Lucene 索引
 */
@Slf4j
@Component
public class ArticleIndexListener {

    @Resource(name = "asyncTaskExecutor")
    private Executor asyncTaskExecutor;

    @EventListener
    public void onArticleIndexEvent(ArticleIndexEvent event) {
        asyncTaskExecutor.execute(() -> {
            try {
                if ("UPDATE".equals(event.getOperation())) {
                    LuceneHelper.upsertArticle(event.getArticle());
                } else if ("DELETE".equals(event.getOperation())) {
                    LuceneHelper.deleteArticle(event.getArticleId());
                }
            } catch (Exception e) {
                log.warn("文章索引同步失败, operation={}, msg={}", event.getOperation(), e.getMessage());
            }
        });
    }
}
