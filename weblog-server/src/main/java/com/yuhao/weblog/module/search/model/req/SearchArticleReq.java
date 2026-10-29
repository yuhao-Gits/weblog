package com.yuhao.weblog.module.search.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 站内搜索请求
 */
@Data
public class SearchArticleReq {
    /** 搜索关键词 */
    @NotBlank(message = "搜索关键词不能为空")
    private String keyword;
}
