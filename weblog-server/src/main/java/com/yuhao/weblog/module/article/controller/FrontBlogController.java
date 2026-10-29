package com.yuhao.weblog.module.article.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.BlogSettingsVO;
import com.yuhao.weblog.module.admin.model.vo.DashboardStatisticsRspVO;
import com.yuhao.weblog.module.admin.service.BlogSettingsService;
import com.yuhao.weblog.module.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/blog")
@RequiredArgsConstructor
public class FrontBlogController {

    private final BlogSettingsService blogSettingsService;
    private final DashboardService dashboardService;

    @GetMapping("/settings/detail")
    public Result<BlogSettingsVO> detail() {
        return blogSettingsService.findDetail();
    }

    /** 前台博主信息卡片：文章 / 分类 / 标签 / 总访问量 */
    @GetMapping("/statistics")
    public Result<DashboardStatisticsRspVO> statistics() {
        return dashboardService.articleCategoryTagStatistics();
    }
}
