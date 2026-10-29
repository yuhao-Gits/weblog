package com.yuhao.weblog.module.article.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.article.model.req.BasePageReq;
import com.yuhao.weblog.module.article.model.req.FindIndexArticlePageListReq;
import com.yuhao.weblog.module.article.model.vo.FindArchiveArticlePageListRspVO;
import com.yuhao.weblog.module.article.model.vo.FindArticleDetailRspVO;
import com.yuhao.weblog.module.article.model.vo.FindIndexArticlePageListRspVO;
import com.yuhao.weblog.module.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class FrontArticleController {

    private final ArticleQueryService articleQueryService;

    @PostMapping("/list")
    public Result<PageResult<FindIndexArticlePageListRspVO>> list(@RequestBody(required = false) FindIndexArticlePageListReq req) {
        return articleQueryService.findIndexArticlePageList(req);
    }

    @GetMapping("/detail")
    public Result<FindArticleDetailRspVO> detail(@RequestParam("articleId") Long articleId) {
        return articleQueryService.findArticleDetail(articleId);
    }

    /** 归档：按年份分组（全量） */
    @PostMapping("/archives")
    public Result<FindArchiveArticlePageListRspVO> archives() {
        return articleQueryService.findArchiveArticlePageList();
    }

    /** 归档：分页 */
    @PostMapping("/archive")
    public Result<PageResult<FindIndexArticlePageListRspVO>> archive(@RequestBody(required = false) BasePageReq req) {
        return articleQueryService.findArchiveArticlePage(req);
    }
}
