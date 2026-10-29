package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.PublishArticleReq;
import com.yuhao.weblog.module.admin.model.req.QueryArticleReq;
import com.yuhao.weblog.module.admin.model.vo.ArticleDetailVO;
import com.yuhao.weblog.module.admin.model.vo.ArticlePageVO;
import com.yuhao.weblog.module.admin.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/article")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/publish")
    public Result<?> publish(@RequestBody @Valid PublishArticleReq req) {
        return articleService.publishArticle(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("articleId") Long articleId) {
        return articleService.deleteArticle(articleId);
    }

    @PostMapping("/list")
    public Result<PageResult<ArticlePageVO>> list(@RequestBody QueryArticleReq req) {
        return articleService.listArticle(req);
    }

    @GetMapping("/detail")
    public Result<ArticleDetailVO> detail(@RequestParam("articleId") Long articleId) {
        return articleService.findArticleDetail(articleId);
    }

    /** 更新文章置顶状态 */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/top/update")
    public Result<?> updateIsTop(@RequestParam("articleId") Long articleId,
                                 @RequestParam("isTop") Integer isTop) {
        return articleService.updateArticleIsTop(articleId, isTop);
    }
}
