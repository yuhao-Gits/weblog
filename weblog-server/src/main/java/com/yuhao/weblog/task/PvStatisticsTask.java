package com.yuhao.weblog.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.StatisticsMapper;
import com.yuhao.weblog.module.admin.model.entity.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 定时任务：每日零点确保当日 PV 记录存在，并校准文章总数快照
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PvStatisticsTask {

    private final StatisticsMapper statisticsMapper;
    private final ArticleMapper articleMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void initTodayStatistics() {
        LocalDate today = LocalDate.now();
        Statistics exist = statisticsMapper.selectById(today);
        if (exist != null) {
            return;
        }
        Statistics statistics = new Statistics();
        statistics.setDate(today);
        statistics.setPv(0L);
        statistics.setUv(0L);
        Long articleCount = articleMapper.selectCount(Wrappers.emptyWrapper());
        statistics.setArticleCount(articleCount == null ? 0L : articleCount);
        statisticsMapper.insert(statistics);
        log.info("已初始化当日访问统计记录, date={}", today);
    }
}
