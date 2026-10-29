package com.yuhao.weblog.module.knowledge.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.knowledge.model.req.QueryArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.req.SortDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.vo.ArticleVO;
import com.yuhao.weblog.module.knowledge.model.vo.DirectoryVO;
import com.yuhao.weblog.module.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台知识库管理接口
 */
@RestController
@RequestMapping("/admin/knowledge")
@RequiredArgsConstructor
public class AdminKnowledgeController {

    private final KnowledgeService knowledgeService;

    // ===== 目录 =====

    @PostMapping("/directory/list")
    public Result<List<DirectoryVO>> listDirectory() {
        return knowledgeService.listDirectory();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/directory/save")
    public Result<?> saveDirectory(@RequestBody SaveDirectoryReq req) {
        return knowledgeService.saveDirectory(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/directory/delete")
    public Result<?> deleteDirectory(@RequestParam("id") Long id) {
        return knowledgeService.deleteDirectory(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/directory/sort")
    public Result<?> sortDirectory(@RequestBody List<SortDirectoryReq> reqs) {
        return knowledgeService.sortDirectory(reqs);
    }

    // ===== 文章 =====

    @PostMapping("/article/list")
    public Result<PageResult<ArticleVO>> pageArticle(@RequestBody QueryArticleReq req) {
        return knowledgeService.pageArticle(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/article/save")
    public Result<?> saveArticle(@RequestBody SaveArticleReq req) {
        return knowledgeService.saveArticle(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/article/delete")
    public Result<?> deleteArticle(@RequestParam("id") Long id) {
        return knowledgeService.deleteArticle(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/article/top")
    public Result<?> updateTop(@RequestParam("id") Long id, @RequestParam("isTop") Integer isTop) {
        return knowledgeService.updateTop(id, isTop);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/article/status")
    public Result<?> updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        return knowledgeService.updateStatus(id, status);
    }

    @PostMapping("/article/detail")
    public Result<ArticleVO> getArticle(@RequestParam("id") Long id) {
        return knowledgeService.getArticle(id);
    }
}
