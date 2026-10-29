package com.yuhao.weblog.module.article.event;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.StatisticsMapper;
import com.yuhao.weblog.module.admin.model.entity.Article;
import com.yuhao.weblog.module.admin.model.entity.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.concurrent.Executor;

/**
 * 文章阅读事件监听：累加阅读量与当日 PV。
 * 计数逻辑提交到独立线程池异步执行，不阻塞详情页响应。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReadArticleEventListener {

    private final ArticleMapper articleMapper;
    private final StatisticsMapper statisticsMapper;

    @Resource(name = "asyncTaskExecutor")
    private Executor asyncTaskExecutor;

    @EventListener
    public void onReadArticle(ReadArticleEvent event) {
        Long articleId = event.getArticleId();
        if (articleId == null) {
            return;
        }
        asyncTaskExecutor.execute(() -> {
            try {
                increaseReadCount(articleId);
                increaseTodayPv();
            } catch (Exception e) {
                log.warn("阅读量统计失败, articleId={}, msg={}", articleId, e.getMessage());
            }
        });
    }

    /** 文章阅读量 +1（单条 UPDATE，DB 层面原子） */
    public void increaseReadCount(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return;
        }
        Article update = new Article();
        update.setId(articleId);
        update.setReadCount((article.getReadCount() == null ? 0L : article.getReadCount()) + 1);
        articleMapper.updateById(update);
    }

    /** 当日 PV +1；当日记录不存在时先创建 */
    public void increaseTodayPv() {
        LocalDate today = LocalDate.now();
        Statistics todaySta = statisticsMapper.selectById(today);
        if (todaySta == null) {
            todaySta = new Statistics();
            todaySta.setDate(today);
            todaySta.setPv(1L);
            todaySta.setUv(1L);
            todaySta.setArticleCount(articleMapper.selectCount(Wrappers.emptyWrapper()));
            statisticsMapper.insert(todaySta);
            return;
        }
        Statistics update = new Statistics();
        update.setDate(today);
        update.setPv((todaySta.getPv() == null ? 0L : todaySta.getPv()) + 1);
        statisticsMapper.updateById(update);
    }
}
