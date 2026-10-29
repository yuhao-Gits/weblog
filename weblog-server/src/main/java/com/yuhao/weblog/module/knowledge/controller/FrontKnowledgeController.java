package com.yuhao.weblog.module.knowledge.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.knowledge.model.vo.ArticleVO;
import com.yuhao.weblog.module.knowledge.model.vo.DirectoryVO;
import com.yuhao.weblog.module.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台知识库接口
 */
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class FrontKnowledgeController {

    private final KnowledgeService knowledgeService;

    /** 目录树（含文章数） */
    @GetMapping("/directory")
    public Result<List<DirectoryVO>> directory() {
        return knowledgeService.frontDirectory();
    }

    /** 文章详情（含上下篇） */
    @GetMapping("/article/detail")
    public Result<ArticleVO> articleDetail(@RequestParam("id") Long id) {
        return knowledgeService.frontArticleDetail(id);
    }
}
