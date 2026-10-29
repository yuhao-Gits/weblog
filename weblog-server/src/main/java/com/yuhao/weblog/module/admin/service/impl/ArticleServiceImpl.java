package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.utils.MarkdownConverter;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.ArticleTagRelMapper;
import com.yuhao.weblog.module.admin.mapper.CategoryMapper;
import com.yuhao.weblog.module.admin.mapper.TagMapper;
import com.yuhao.weblog.module.admin.model.entity.Article;
import com.yuhao.weblog.module.admin.model.entity.ArticleTagRel;
import com.yuhao.weblog.module.admin.model.entity.Category;
import com.yuhao.weblog.module.admin.model.entity.Tag;
import com.yuhao.weblog.module.admin.model.req.PublishArticleReq;
import com.yuhao.weblog.module.admin.model.req.QueryArticleReq;
import com.yuhao.weblog.module.admin.model.vo.ArticleDetailVO;
import com.yuhao.weblog.module.admin.model.vo.ArticlePageVO;
import com.yuhao.weblog.module.admin.service.ArticleService;
import com.yuhao.weblog.module.search.event.ArticleIndexEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagRelMapper articleTagRelMapper;
    private final MarkdownConverter markdownConverter;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> publishArticle(PublishArticleReq req) {
        String contentHtml = markdownConverter.toHtml(req.getContent());
        String summary = StringUtils.isBlank(req.getSummary())
                ? markdownConverter.generateSummary(req.getContent())
                : req.getSummary();

        Article article;
        boolean isUpdate = req.getId() != null;
        if (isUpdate) {
            article = articleMapper.selectById(req.getId());
            if (article == null) {
                throw new BizException("文章不存在");
            }
        } else {
            article = new Article();
            article.setReadCount(0L);
        }
        article.setTitle(req.getTitle());
        article.setCover(req.getCover());
        article.setSummary(summary);
        article.setContent(req.getContent());
        article.setContentHtml(contentHtml);
        article.setCategoryId(req.getCategoryId());
        article.setStatus(1);
        article.setIsTop(req.getIsTop() == null ? 0 : req.getIsTop());

        if (isUpdate) {
            articleMapper.updateById(article);
            articleTagRelMapper.delete(Wrappers.<ArticleTagRel>lambdaQuery()
                    .eq(ArticleTagRel::getArticleId, article.getId()));
        } else {
            articleMapper.insert(article);
        }

        if (!CollectionUtils.isEmpty(req.getTagIds())) {
            for (Long tagId : req.getTagIds()) {
                ArticleTagRel rel = new ArticleTagRel();
                rel.setArticleId(article.getId());
                rel.setTagId(tagId);
                articleTagRelMapper.insert(rel);
            }
        }
        // 同步 Lucene 索引（新增/更新）
        eventPublisher.publishEvent(ArticleIndexEvent.update(this, article));
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteArticle(Long articleId) {
        articleMapper.deleteById(articleId);
        articleTagRelMapper.delete(Wrappers.<ArticleTagRel>lambdaQuery()
                .eq(ArticleTagRel::getArticleId, articleId));
        // 同步删除 Lucene 索引
        eventPublisher.publishEvent(ArticleIndexEvent.delete(this, articleId));
        return Result.success();
    }

    @Override
    public Result<PageResult<ArticlePageVO>> listArticle(QueryArticleReq req) {
        Page<Article> page = articleMapper.selectPage(
                new Page<>(req.getCurrent(), req.getSize()),
                Wrappers.<Article>lambdaQuery()
                        .like(StringUtils.isNotBlank(req.getTitle()), Article::getTitle, req.getTitle())
                        .eq(req.getCategoryId() != null, Article::getCategoryId, req.getCategoryId())
                        .eq(req.getStatus() != null, Article::getStatus, req.getStatus())
                        .ge(req.getStartDate() != null, Article::getCreateTime, req.getStartDate())
                        .le(req.getEndDate() != null, Article::getCreateTime, req.getEndDate())
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));

        Map<Long, String> categoryNameMap = categoryMapper.selectList(Wrappers.emptyWrapper())
                .stream().collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));

        List<ArticlePageVO> vos = page.getRecords().stream().map(a -> {
            ArticlePageVO vo = new ArticlePageVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setCover(a.getCover());
            vo.setSummary(a.getSummary());
            vo.setCategoryId(a.getCategoryId());
            vo.setCategoryName(categoryNameMap.getOrDefault(a.getCategoryId(), ""));
            vo.setReadCount(a.getReadCount());
            vo.setIsTop(a.getIsTop() == null ? 0 : a.getIsTop());
            vo.setCreateTime(a.getCreateTime());
            vo.setTagNames(buildTagNames(a.getId()));
            return vo;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    public Result<ArticleDetailVO> findArticleDetail(Long articleId) {
        Article a = articleMapper.selectById(articleId);
        if (a == null) {
            throw new BizException("文章不存在");
        }
        ArticleDetailVO vo = new ArticleDetailVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setCover(a.getCover());
        vo.setSummary(a.getSummary());
        vo.setContent(a.getContent());
        vo.setContentHtml(a.getContentHtml());
        vo.setCategoryId(a.getCategoryId());
        Category category = categoryMapper.selectById(a.getCategoryId());
        vo.setCategoryName(category == null ? "" : category.getName());
        vo.setReadCount(a.getReadCount());
        vo.setIsTop(a.getIsTop() == null ? 0 : a.getIsTop());
        vo.setCreateTime(a.getCreateTime());

        List<Long> tagIds = articleTagRelMapper.selectList(
                Wrappers.<ArticleTagRel>lambdaQuery().eq(ArticleTagRel::getArticleId, articleId))
                .stream().map(ArticleTagRel::getTagId).collect(Collectors.toList());
        vo.setTagIds(tagIds);
        vo.setTagNames(buildTagNames(articleId));
        return Result.success(vo);
    }

    @Override
    public Result<?> updateArticleIsTop(Long articleId, Integer isTop) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BizException("文章不存在");
        }
        Article update = new Article();
        update.setId(articleId);
        update.setIsTop(isTop == null || isTop != 1 ? 0 : 1);
        articleMapper.updateById(update);
        return Result.success();
    }

    /**
     * 根据文章 ID 拼接标签名（逗号分隔）
     */
    private String buildTagNames(Long articleId) {
        List<Long> tagIds = articleTagRelMapper.selectList(
                Wrappers.<ArticleTagRel>lambdaQuery().eq(ArticleTagRel::getArticleId, articleId))
                .stream().map(ArticleTagRel::getTagId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIds)) {
            return "";
        }
        return tagMapper.selectBatchIds(tagIds).stream()
                .map(Tag::getName).collect(Collectors.joining(", "));
    }
}
