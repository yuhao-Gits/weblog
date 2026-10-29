package com.yuhao.weblog.module.search.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.search.model.req.SearchArticleReq;
import com.yuhao.weblog.module.search.model.vo.SearchArticleRspVO;
import com.yuhao.weblog.module.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 前台站内搜索接口
 */
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class FrontSearchController {

    private final SearchService searchService;

    @PostMapping("/article")
    public Result<List<SearchArticleRspVO>> search(@RequestBody @Valid SearchArticleReq req) {
        return Result.success(searchService.search(req.getKeyword()));
    }
}
