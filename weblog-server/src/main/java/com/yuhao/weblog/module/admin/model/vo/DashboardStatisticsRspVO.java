package com.yuhao.weblog.module.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsRspVO {
    /** 文章总数 */
    private Long articleTotal;
    /** 分类总数 */
    private Long categoryTotal;
    /** 标签总数 */
    private Long tagTotal;
    /** 访问量（总 PV） */
    private Long viewTotal;
}
