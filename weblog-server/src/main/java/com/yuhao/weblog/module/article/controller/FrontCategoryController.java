package com.yuhao.weblog.module.article.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;
import com.yuhao.weblog.module.article.model.req.FindCategoryArticlePageListReq;
import com.yuhao.weblog.module.article.model.vo.FindIndexArticlePageListRspVO;
import com.yuhao.weblog.module.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class FrontCategoryController {

    private final ArticleQueryService articleQueryService;

    @GetMapping("/list")
    public Result<List<CategoryVO>> list() {
        return articleQueryService.findCategoryList();
    }

    /** 某分类下的文章分页列表 */
    @PostMapping("/article/list")
    public Result<PageResult<FindIndexArticlePageListRspVO>> articleList(
            @RequestBody FindCategoryArticlePageListReq req) {
        return articleQueryService.findCategoryArticlePageList(req);
    }
}
