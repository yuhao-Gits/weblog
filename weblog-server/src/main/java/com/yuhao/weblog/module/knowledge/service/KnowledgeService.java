package com.yuhao.weblog.module.knowledge.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.knowledge.model.req.QueryArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.req.SortDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.vo.ArticleVO;
import com.yuhao.weblog.module.knowledge.model.vo.DirectoryVO;

import java.util.List;

/**
 * 知识库服务
 */
public interface KnowledgeService {

    // ===== 目录 =====

    /** 查询目录树（后台） */
    Result<List<DirectoryVO>> listDirectory();

    /** 新增/编辑目录 */
    Result<?> saveDirectory(SaveDirectoryReq req);

    /** 删除目录 */
    Result<?> deleteDirectory(Long id);

    /** 更新目录排序（拖拽） */
    Result<?> sortDirectory(List<SortDirectoryReq> reqs);

    // ===== 文章 =====

    /** 分页查询文章（后台） */
    Result<PageResult<ArticleVO>> pageArticle(QueryArticleReq req);

    /** 新增/编辑文章 */
    Result<?> saveArticle(SaveArticleReq req);

    /** 删除文章 */
    Result<?> deleteArticle(Long id);

    /** 更新置顶状态 */
    Result<?> updateTop(Long id, Integer isTop);

    /** 更新发布状态 */
    Result<?> updateStatus(Long id, Integer status);

    /** 文章详情（后台编辑回显） */
    Result<ArticleVO> getArticle(Long id);

    // ===== 前台 =====

    /** 前台：目录树（含文章列表） */
    Result<List<DirectoryVO>> frontDirectory();

    /** 前台：文章详情（含上下篇） */
    Result<ArticleVO> frontArticleDetail(Long id);
}
