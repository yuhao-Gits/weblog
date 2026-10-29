package com.yuhao.weblog.module.admin.model.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ArticlePublishHotRspVO {
    private LocalDate date;
    private Long count;
}
