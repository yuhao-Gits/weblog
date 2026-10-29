package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class PvStatisticsRspVO {
    private List<String> dates;
    private List<Long> pv;
    private List<Long> uv;
}
