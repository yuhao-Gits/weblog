package com.yuhao.weblog.module.article.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.TagVO;
import com.yuhao.weblog.module.article.model.req.FindTagArticlePageListReq;
import com.yuhao.weblog.module.article.model.vo.FindIndexArticlePageListRspVO;
import com.yuhao.weblog.module.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class FrontTagController {

    private final ArticleQueryService articleQueryService;

    @GetMapping("/list")
    public Result<List<TagVO>> list() {
        return articleQueryService.findTagList();
    }

    /** 某标签下的文章分页列表 */
    @PostMapping("/article/list")
    public Result<PageResult<FindIndexArticlePageListRspVO>> articleList(
            @RequestBody FindTagArticlePageListReq req) {
        return articleQueryService.findTagArticlePageList(req);
    }
}
