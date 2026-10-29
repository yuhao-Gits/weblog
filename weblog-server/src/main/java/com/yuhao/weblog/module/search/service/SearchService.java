package com.yuhao.weblog.module.search.service;

import com.yuhao.weblog.module.search.model.vo.SearchArticleRspVO;

import java.util.List;

/**
 * 站内搜索服务
 */
public interface SearchService {

    /**
     * 根据关键词检索文章
     */
    List<SearchArticleRspVO> search(String keyword);
}
