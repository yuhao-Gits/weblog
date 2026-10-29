package com.yuhao.weblog.module.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.ArticleTagRelMapper;
import com.yuhao.weblog.module.admin.mapper.CategoryMapper;
import com.yuhao.weblog.module.admin.mapper.TagMapper;
import com.yuhao.weblog.module.admin.model.entity.Article;
import com.yuhao.weblog.module.admin.model.entity.ArticleTagRel;
import com.yuhao.weblog.module.admin.model.entity.Category;
import com.yuhao.weblog.module.admin.model.entity.Tag;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;
import com.yuhao.weblog.module.admin.model.vo.TagVO;
import com.yuhao.weblog.module.article.event.ReadArticleEvent;
import com.yuhao.weblog.module.article.model.req.BasePageReq;
import com.yuhao.weblog.module.article.model.req.FindCategoryArticlePageListReq;
import com.yuhao.weblog.module.article.model.req.FindIndexArticlePageListReq;
import com.yuhao.weblog.module.article.model.req.FindTagArticlePageListReq;
import com.yuhao.weblog.module.article.model.vo.FindArchiveArticlePageListRspVO;
import com.yuhao.weblog.module.article.model.vo.FindArticleDetailRspVO;
import com.yuhao.weblog.module.article.model.vo.FindIndexArticlePageListRspVO;
import com.yuhao.weblog.module.article.service.ArticleQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleQueryServiceImpl implements ArticleQueryService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagRelMapper articleTagRelMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Result<PageResult<FindIndexArticlePageListRspVO>> findIndexArticlePageList(FindIndexArticlePageListReq req) {
        if (req == null) {
            req = new FindIndexArticlePageListReq();
        }
        Long current = req.getCurrent() == null || req.getCurrent() < 1 ? 1L : req.getCurrent();
        Long size = req.getSize() == null || req.getSize() < 1 ? 10L : req.getSize();

        List<Long> articleIdsByTag = null;
        if (req.getTagId() != null) {
            articleIdsByTag = queryArticleIdsByTagId(req.getTagId());
        }

        Page<Article> page = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<Article>lambdaQuery()
                        .eq(req.getCategoryId() != null, Article::getCategoryId, req.getCategoryId())
                        .in(articleIdsByTag != null, Article::getId,
                                articleIdsByTag == null ? Collections.emptyList() : articleIdsByTag)
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));

        return Result.success(buildPageResult(page));
    }

    @Override
    public Result<FindArticleDetailRspVO> findArticleDetail(Long articleId) {
        Article a = articleMapper.selectById(articleId);
        if (a == null) {
            return Result.fail("文章不存在");
        }
        // 阅读量通过事件异步 +1，避免阻塞详情接口
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        FindArticleDetailRspVO vo = new FindArticleDetailRspVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setCover(a.getCover());
        vo.setSummary(a.getSummary());
        vo.setContentHtml(a.getContentHtml());
        vo.setReadCount(a.getReadCount() == null ? 0L : a.getReadCount() + 1);
        vo.setCreateTime(a.getCreateTime());
        Category category = categoryMapper.selectById(a.getCategoryId());
        vo.setCategoryName(category == null ? "" : category.getName());
        vo.setTagNames(buildTagNames(articleId));
        return Result.success(vo);
    }

    @Override
    public Result<FindArchiveArticlePageListRspVO> findArchiveArticlePageList() {
        List<Article> articles = articleMapper.selectList(
                Wrappers.<Article>lambdaQuery()
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));

        Map<Integer, List<Article>> byYear = articles.stream()
                .filter(a -> a.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreateTime().getYear(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<FindArchiveArticlePageListRspVO.ArchiveGroup> groups = new ArrayList<>();
        for (Map.Entry<Integer, List<Article>> entry : byYear.entrySet()) {
            FindArchiveArticlePageListRspVO.ArchiveGroup group = new FindArchiveArticlePageListRspVO.ArchiveGroup();
            group.setYear(entry.getKey());
            List<FindArchiveArticlePageListRspVO.ArchiveItem> items = entry.getValue().stream().map(a -> {
                FindArchiveArticlePageListRspVO.ArchiveItem item = new FindArchiveArticlePageListRspVO.ArchiveItem();
                item.setId(a.getId());
                item.setTitle(a.getTitle());
                item.setCreateTime(a.getCreateTime());
                return item;
            }).collect(Collectors.toList());
            group.setArticles(items);
            groups.add(group);
        }

        FindArchiveArticlePageListRspVO rsp = new FindArchiveArticlePageListRspVO();
        rsp.setArchives(groups);
        return Result.success(rsp);
    }

    @Override
    public Result<PageResult<FindIndexArticlePageListRspVO>> findArchiveArticlePage(BasePageReq req) {
        if (req == null) {
            req = new BasePageReq();
        }
        Long current = req.getCurrent() == null || req.getCurrent() < 1 ? 1L : req.getCurrent();
        Long size = req.getSize() == null || req.getSize() < 1 ? 10L : req.getSize();

        Page<Article> page = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<Article>lambdaQuery()
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));
        return Result.success(buildPageResult(page));
    }

    @Override
    public Result<PageResult<FindIndexArticlePageListRspVO>> findCategoryArticlePageList(FindCategoryArticlePageListReq req) {
        if (req == null || StringUtils.isBlank(req.getCategoryName())) {
            return Result.fail("分类名称不能为空");
        }
        Category category = categoryMapper.selectOne(
                Wrappers.<Category>lambdaQuery().eq(Category::getName, req.getCategoryName()));
        if (category == null) {
            return Result.success(PageResult.of(Collections.emptyList(), 0L));
        }

        Long current = req.getCurrent() == null || req.getCurrent() < 1 ? 1L : req.getCurrent();
        Long size = req.getSize() == null || req.getSize() < 1 ? 10L : req.getSize();

        Page<Article> page = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<Article>lambdaQuery()
                        .eq(Article::getCategoryId, category.getId())
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));
        return Result.success(buildPageResult(page));
    }

    @Override
    public Result<PageResult<FindIndexArticlePageListRspVO>> findTagArticlePageList(FindTagArticlePageListReq req) {
        if (req == null || StringUtils.isBlank(req.getTagName())) {
            return Result.fail("标签名称不能为空");
        }
        Tag tag = tagMapper.selectOne(Wrappers.<Tag>lambdaQuery().eq(Tag::getName, req.getTagName()));
        if (tag == null) {
            return Result.success(PageResult.of(Collections.emptyList(), 0L));
        }

        List<Long> articleIds = queryArticleIdsByTagId(tag.getId());
        if (CollectionUtils.isEmpty(articleIds)) {
            return Result.success(PageResult.of(Collections.emptyList(), 0L));
        }

        Long current = req.getCurrent() == null || req.getCurrent() < 1 ? 1L : req.getCurrent();
        Long size = req.getSize() == null || req.getSize() < 1 ? 10L : req.getSize();

        Page<Article> page = articleMapper.selectPage(
                new Page<>(current, size),
                Wrappers.<Article>lambdaQuery()
                        .in(Article::getId, articleIds)
                        .orderByDesc(Article::getIsTop)
                        .orderByDesc(Article::getCreateTime));
        return Result.success(buildPageResult(page));
    }

    @Override
    public Result<List<CategoryVO>> findCategoryList() {
        List<Category> categories = categoryMapper.selectList(
                Wrappers.<Category>lambdaQuery().orderByDesc(Category::getCreateTime));
        List<CategoryVO> vos = categories.stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setCreateTime(c.getCreateTime());
            vo.setArticlesTotal(countArticlesByCategoryId(c.getId()));
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Override
    public Result<List<TagVO>> findTagList() {
        List<Tag> tags = tagMapper.selectList(Wrappers.<Tag>lambdaQuery().orderByDesc(Tag::getCreateTime));
        List<TagVO> vos = tags.stream().map(t -> {
            TagVO vo = new TagVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setCreateTime(t.getCreateTime());
            vo.setArticlesTotal(countArticlesByTagId(t.getId()));
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    // ====================== 私有方法 ======================

    private PageResult<FindIndexArticlePageListRspVO> buildPageResult(Page<Article> page) {
        Map<Long, String> categoryNameMap = categoryMapper.selectList(Wrappers.emptyWrapper())
                .stream().collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));

        List<FindIndexArticlePageListRspVO> vos = page.getRecords().stream().map(a -> {
            FindIndexArticlePageListRspVO vo = new FindIndexArticlePageListRspVO();
            vo.setId(a.getId());
            vo.setTitle(a.getTitle());
            vo.setCover(a.getCover());
            vo.setSummary(a.getSummary());
            vo.setReadCount(a.getReadCount());
            vo.setCreateTime(a.getCreateTime());
            vo.setCategoryName(categoryNameMap.getOrDefault(a.getCategoryId(), ""));
            vo.setTagNames(buildTagNames(a.getId()));
            vo.setIsTop(a.getIsTop() == null ? 0 : a.getIsTop());
            return vo;
        }).collect(Collectors.toList());

        PageResult<FindIndexArticlePageListRspVO> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setRecords(vos);
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }

    private List<Long> queryArticleIdsByTagId(Long tagId) {
        return articleTagRelMapper.selectList(
                        Wrappers.<ArticleTagRel>lambdaQuery().eq(ArticleTagRel::getTagId, tagId))
                .stream().map(ArticleTagRel::getArticleId).collect(Collectors.toList());
    }

    private Long countArticlesByCategoryId(Long categoryId) {
        Long total = articleMapper.selectCount(Wrappers.<Article>lambdaQuery()
                .eq(Article::getCategoryId, categoryId));
        return total == null ? 0L : total;
    }

    private Long countArticlesByTagId(Long tagId) {
        return articleTagRelMapper.selectCount(Wrappers.<ArticleTagRel>lambdaQuery()
                .eq(ArticleTagRel::getTagId, tagId));
    }

    private String buildTagNames(Long articleId) {
        List<Long> tagIds = queryTagIdsByArticleId(articleId);
        if (tagIds.isEmpty()) {
            return "";
        }
        return tagMapper.selectBatchIds(tagIds).stream()
                .map(Tag::getName).collect(Collectors.joining(", "));
    }

    private List<Long> queryTagIdsByArticleId(Long articleId) {
        if (articleId == null) {
            return Collections.emptyList();
        }
        return articleTagRelMapper.selectList(
                        Wrappers.<ArticleTagRel>lambdaQuery().eq(ArticleTagRel::getArticleId, articleId))
                .stream().map(ArticleTagRel::getTagId).collect(Collectors.toList());
    }
}
