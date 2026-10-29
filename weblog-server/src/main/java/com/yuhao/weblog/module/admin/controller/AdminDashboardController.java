package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.ArticlePublishHotRspVO;
import com.yuhao.weblog.module.admin.model.vo.DashboardStatisticsRspVO;
import com.yuhao.weblog.module.admin.model.vo.PvStatisticsRspVO;
import com.yuhao.weblog.module.admin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/article/category/tag/statistics")
    public Result<DashboardStatisticsRspVO> statistics() {
        return dashboardService.articleCategoryTagStatistics();
    }

    @GetMapping("/article/publish/hot")
    public Result<List<ArticlePublishHotRspVO>> publishHot() {
        return dashboardService.articlePublishHot();
    }

    @GetMapping("/pv/statistics")
    public Result<PvStatisticsRspVO> pvStatistics() {
        return dashboardService.pvStatistics();
    }
}
