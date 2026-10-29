package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.PublishArticleReq;
import com.yuhao.weblog.module.admin.model.req.QueryArticleReq;
import com.yuhao.weblog.module.admin.model.vo.ArticleDetailVO;
import com.yuhao.weblog.module.admin.model.vo.ArticlePageVO;

public interface ArticleService {

    /**
     * 发布 / 更新文章（id 为空=新增）
     */
    Result<?> publishArticle(PublishArticleReq req);

    /**
     * 删除文章
     */
    Result<?> deleteArticle(Long articleId);

    /**
     * 文章分页列表（后台）
     */
    Result<PageResult<ArticlePageVO>> listArticle(QueryArticleReq req);

    /**
     * 文章详情（后台编辑用）
     */
    Result<ArticleDetailVO> findArticleDetail(Long articleId);

    /**
     * 更新文章置顶状态
     */
    Result<?> updateArticleIsTop(Long articleId, Integer isTop);
}
