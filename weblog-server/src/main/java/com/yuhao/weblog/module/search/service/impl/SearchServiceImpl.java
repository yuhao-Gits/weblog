package com.yuhao.weblog.module.search.service.impl;

import com.yuhao.weblog.common.utils.LuceneHelper;
import com.yuhao.weblog.module.search.model.vo.SearchArticleRspVO;
import com.yuhao.weblog.module.search.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内搜索服务实现
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final int MAX_RESULTS = 20;

    @Override
    public List<SearchArticleRspVO> search(String keyword) {
        List<LuceneHelper.SearchResult> results = LuceneHelper.search(keyword.trim(), MAX_RESULTS);
        return results.stream().map(r -> {
            SearchArticleRspVO vo = new SearchArticleRspVO();
            vo.setId(r.getId());
            vo.setTitle(r.getTitle());
            vo.setHighlight(r.getHighlight());
            return vo;
        }).collect(Collectors.toList());
    }
}
