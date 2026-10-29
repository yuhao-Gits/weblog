package com.yuhao.weblog.module.search;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.common.utils.LuceneHelper;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.model.entity.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 应用启动后初始化 Lucene 文章索引
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class LuceneIndexRunner implements CommandLineRunner {

    private final ArticleMapper articleMapper;

    @Override
    public void run(String... args) {
        try {
            List<Article> articles = articleMapper.selectList(Wrappers.emptyWrapper());
            LuceneHelper.rebuildIndex(articles);
        } catch (Exception e) {
            log.error("初始化 Lucene 索引失败", e);
        }
    }
}
