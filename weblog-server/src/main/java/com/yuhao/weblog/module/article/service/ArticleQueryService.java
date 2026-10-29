package com.yuhao.weblog.module.article.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;
import com.yuhao.weblog.module.admin.model.vo.TagVO;
import com.yuhao.weblog.module.article.model.req.BasePageReq;
import com.yuhao.weblog.module.article.model.req.FindCategoryArticlePageListReq;
import com.yuhao.weblog.module.article.model.req.FindIndexArticlePageListReq;
import com.yuhao.weblog.module.article.model.req.FindTagArticlePageListReq;
import com.yuhao.weblog.module.article.model.vo.FindArchiveArticlePageListRspVO;
import com.yuhao.weblog.module.article.model.vo.FindArticleDetailRspVO;
import com.yuhao.weblog.module.article.model.vo.FindIndexArticlePageListRspVO;

import java.util.List;

public interface ArticleQueryService {

    /**
     * 前台首页文章分页（可按分类 / 标签筛选），置顶文章排最前
     */
    Result<PageResult<FindIndexArticlePageListRspVO>> findIndexArticlePageList(FindIndexArticlePageListReq req);

    /**
     * 文章详情（异步累加阅读量）
     */
    Result<FindArticleDetailRspVO> findArticleDetail(Long articleId);

    /**
     * 文章归档：按年份分组（全量，用于归档页概览）
     */
    Result<FindArchiveArticlePageListRspVO> findArchiveArticlePageList();

    /**
     * 文章归档：分页
     */
    Result<PageResult<FindIndexArticlePageListRspVO>> findArchiveArticlePage(BasePageReq req);

    /**
     * 某分类下的文章分页
     */
    Result<PageResult<FindIndexArticlePageListRspVO>> findCategoryArticlePageList(FindCategoryArticlePageListReq req);

    /**
     * 某标签下的文章分页
     */
    Result<PageResult<FindIndexArticlePageListRspVO>> findTagArticlePageList(FindTagArticlePageListReq req);

    /**
     * 全部分类（含文章数）
     */
    Result<List<CategoryVO>> findCategoryList();

    /**
     * 全部标签（含文章数）
     */
    Result<List<TagVO>> findTagList();
}
