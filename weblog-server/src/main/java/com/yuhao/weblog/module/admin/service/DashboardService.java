package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.ArticlePublishHotRspVO;
import com.yuhao.weblog.module.admin.model.vo.DashboardStatisticsRspVO;
import com.yuhao.weblog.module.admin.model.vo.PvStatisticsRspVO;

import java.util.List;

public interface DashboardService {

    Result<DashboardStatisticsRspVO> articleCategoryTagStatistics();

    Result<List<ArticlePublishHotRspVO>> articlePublishHot();

    Result<PvStatisticsRspVO> pvStatistics();
}
