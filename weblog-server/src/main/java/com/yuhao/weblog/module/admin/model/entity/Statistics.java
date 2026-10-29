package com.yuhao.weblog.module.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 访问统计表（用于仪表盘近一周 PV）
 */
@Data
@TableName("t_statistics")
public class Statistics {

    /** 日期（主键，如 2024-01-01） */
    @TableId(type = IdType.INPUT)
    private LocalDate date;

    /** 当日 PV */
    private Long pv;

    /** 当日 UV */
    private Long uv;

    /** 当日文章总数（快照） */
    private Long articleCount;
}
