package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.CategoryMapper;
import com.yuhao.weblog.module.admin.mapper.StatisticsMapper;
import com.yuhao.weblog.module.admin.mapper.TagMapper;
import com.yuhao.weblog.module.admin.model.entity.Article;
import com.yuhao.weblog.module.admin.model.entity.Statistics;
import com.yuhao.weblog.module.admin.model.vo.ArticlePublishHotRspVO;
import com.yuhao.weblog.module.admin.model.vo.DashboardStatisticsRspVO;
import com.yuhao.weblog.module.admin.model.vo.PvStatisticsRspVO;
import com.yuhao.weblog.module.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final StatisticsMapper statisticsMapper;

    @Override
    public Result<DashboardStatisticsRspVO> articleCategoryTagStatistics() {
        long articleTotal = articleMapper.selectCount(Wrappers.emptyWrapper());
        long categoryTotal = categoryMapper.selectCount(Wrappers.emptyWrapper());
        long tagTotal = tagMapper.selectCount(Wrappers.emptyWrapper());
        Long pvSum = statisticsMapper.selectList(Wrappers.emptyWrapper()).stream()
                .map(Statistics::getPv).reduce(0L, Long::sum);
        DashboardStatisticsRspVO vo = new DashboardStatisticsRspVO(articleTotal, categoryTotal, tagTotal, pvSum);
        return Result.success(vo);
    }

    @Override
    public Result<List<ArticlePublishHotRspVO>> articlePublishHot() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        List<Article> articles = articleMapper.selectList(
                Wrappers.<Article>lambdaQuery().ge(Article::getCreateTime, start.atStartOfDay()));

        Map<LocalDate, Long> countMap = articles.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(a -> a.getCreateTime().toLocalDate(), Collectors.counting()));

        List<ArticlePublishHotRspVO> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            ArticlePublishHotRspVO vo = new ArticlePublishHotRspVO();
            vo.setDate(date);
            vo.setCount(countMap.getOrDefault(date, 0L));
            list.add(vo);
        }
        return Result.success(list);
    }

    @Override
    public Result<PvStatisticsRspVO> pvStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(6);
        List<Statistics> stats = statisticsMapper.selectList(
                Wrappers.<Statistics>lambdaQuery().ge(Statistics::getDate, start).orderByAsc(Statistics::getDate));

        Map<LocalDate, Statistics> map = stats.stream()
                .collect(Collectors.toMap(Statistics::getDate, s -> s, (a, b) -> a, LinkedHashMap::new));

        List<String> dates = new ArrayList<>();
        List<Long> pv = new ArrayList<>();
        List<Long> uv = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Statistics s = map.get(date);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            pv.add(s == null ? 0L : s.getPv());
            uv.add(s == null ? 0L : s.getUv());
        }
        PvStatisticsRspVO vo = new PvStatisticsRspVO();
        vo.setDates(dates);
        vo.setPv(pv);
        vo.setUv(uv);
        return Result.success(vo);
    }
}
