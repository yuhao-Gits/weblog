package com.yuhao.weblog.module.knowledge.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.utils.MarkdownConverter;
import com.yuhao.weblog.module.knowledge.mapper.KnowledgeArticleMapper;
import com.yuhao.weblog.module.knowledge.mapper.KnowledgeDirectoryMapper;
import com.yuhao.weblog.module.knowledge.model.entity.KnowledgeArticle;
import com.yuhao.weblog.module.knowledge.model.entity.KnowledgeDirectory;
import com.yuhao.weblog.module.knowledge.model.req.QueryArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveArticleReq;
import com.yuhao.weblog.module.knowledge.model.req.SaveDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.req.SortDirectoryReq;
import com.yuhao.weblog.module.knowledge.model.vo.ArticleVO;
import com.yuhao.weblog.module.knowledge.model.vo.DirectoryVO;
import com.yuhao.weblog.module.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeDirectoryMapper directoryMapper;
    private final KnowledgeArticleMapper articleMapper;
    private final MarkdownConverter markdownConverter;

    // ==================== 目录 ====================

    @Override
    public Result<List<DirectoryVO>> listDirectory() {
        List<KnowledgeDirectory> list = directoryMapper.selectList(
                Wrappers.<KnowledgeDirectory>lambdaQuery()
                        .orderByAsc(KnowledgeDirectory::getSort)
                        .orderByAsc(KnowledgeDirectory::getCreateTime));
        // 统计每个目录下文章数
        Map<Long, Long> countMap = articleMapper.selectList(null).stream()
                .collect(Collectors.groupingBy(KnowledgeArticle::getDirectoryId, Collectors.counting()));
        return Result.success(buildDirectoryTree(list, countMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> saveDirectory(SaveDirectoryReq req) {
        if (StringUtils.isBlank(req.getTitle())) {
            throw new BizException("目录标题不能为空");
        }
        Long parentId = req.getParentId() == null ? 0L : req.getParentId();
        if (req.getId() == null) {
            KnowledgeDirectory dir = new KnowledgeDirectory();
            dir.setTitle(req.getTitle().trim());
            dir.setParentId(parentId);
            dir.setSort(req.getSort() == null ? 0 : req.getSort());
            directoryMapper.insert(dir);
        } else {
            KnowledgeDirectory dir = directoryMapper.selectById(req.getId());
            if (dir == null) {
                throw new BizException("目录不存在");
            }
            dir.setTitle(req.getTitle().trim());
            dir.setParentId(parentId);
            if (req.getSort() != null) {
                dir.setSort(req.getSort());
            }
            directoryMapper.updateById(dir);
        }
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> deleteDirectory(Long id) {
        if (id == null) {
            throw new BizException("目录 ID 不能为空");
        }
        // 存在子目录不允许删除
        Long childCount = directoryMapper.selectCount(
                Wrappers.<KnowledgeDirectory>lambdaQuery().eq(KnowledgeDirectory::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BizException("该目录下存在子目录，请先删除子目录");
        }
        // 存在文章不允许删除
        Long articleCount = articleMapper.selectCount(
                Wrappers.<KnowledgeArticle>lambdaQuery().eq(KnowledgeArticle::getDirectoryId, id));
        if (articleCount != null && articleCount > 0) {
            throw new BizException("该目录下存在文章，请先删除或移动文章");
        }
        directoryMapper.deleteById(id);
        return Result.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> sortDirectory(List<SortDirectoryReq> reqs) {
        if (reqs == null || reqs.isEmpty()) {
            return Result.success();
        }
        for (SortDirectoryReq req : reqs) {
            KnowledgeDirectory dir = directoryMapper.selectById(req.getId());
            if (dir == null) {
                continue;
            }
            dir.setSort(req.getSort());
            if (req.getParentId() != null) {
                dir.setParentId(req.getParentId());
            }
            directoryMapper.updateById(dir);
        }
        return Result.success();
    }

    // ==================== 文章 ====================

    @Override
    public Result<PageResult<ArticleVO>> pageArticle(QueryArticleReq req) {
        Page<KnowledgeArticle> page = articleMapper.selectPage(
                new Page<>(req.getCurrent() == null ? 1 : req.getCurrent(),
                        req.getSize() == null ? 10 : req.getSize()),
                Wrappers.<KnowledgeArticle>lambdaQuery()
                        .eq(req.getDirectoryId() != null, KnowledgeArticle::getDirectoryId, req.getDirectoryId())
                        .eq(req.getStatus() != null, KnowledgeArticle::getStatus, req.getStatus())
                        .like(StringUtils.isNotBlank(req.getTitle()), KnowledgeArticle::getTitle, req.getTitle())
                        .orderByDesc(KnowledgeArticle::getIsTop)
                        .orderByDesc(KnowledgeArticle::getCreateTime));
        List<ArticleVO> vos = page.getRecords().stream().map(this::toArticleVO).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> saveArticle(SaveArticleReq req) {
        if (StringUtils.isBlank(req.getTitle())) {
            throw new BizException("文章标题不能为空");
        }
        if (req.getDirectoryId() == null) {
            throw new BizException("请选择所属目录");
        }
        String html = markdownConverter.toHtml(req.getContent());
        if (req.getId() == null) {
            KnowledgeArticle article = new KnowledgeArticle();
            article.setTitle(req.getTitle().trim());
            article.setContent(req.getContent());
            article.setContentHtml(html);
            article.setSummary(StringUtils.isBlank(req.getSummary())
                    ? markdownConverter.generateSummary(req.getContent()) : req.getSummary());
            article.setDirectoryId(req.getDirectoryId());
            article.setIsTop(req.getIsTop() == null ? 0 : req.getIsTop());
            article.setStatus(req.getStatus() == null ? 0 : req.getStatus());
            articleMapper.insert(article);
        } else {
            KnowledgeArticle article = articleMapper.selectById(req.getId());
            if (article == null) {
                throw new BizException("文章不存在");
            }
            article.setTitle(req.getTitle().trim());
            article.setContent(req.getContent());
            article.setContentHtml(html);
            if (StringUtils.isNotBlank(req.getSummary())) {
                article.setSummary(req.getSummary());
            }
            article.setDirectoryId(req.getDirectoryId());
            if (req.getIsTop() != null) {
                article.setIsTop(req.getIsTop());
            }
            if (req.getStatus() != null) {
                article.setStatus(req.getStatus());
            }
            articleMapper.updateById(article);
        }
        return Result.success();
    }

    @Override
    public Result<?> deleteArticle(Long id) {
        if (id == null) {
            throw new BizException("文章 ID 不能为空");
        }
        articleMapper.deleteById(id);
        return Result.success();
    }

    @Override
    public Result<?> updateTop(Long id, Integer isTop) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BizException("文章不存在");
        }
        KnowledgeArticle update = new KnowledgeArticle();
        update.setId(id);
        update.setIsTop(isTop == null || isTop != 1 ? 0 : 1);
        articleMapper.updateById(update);
        return Result.success();
    }

    @Override
    public Result<?> updateStatus(Long id, Integer status) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BizException("文章不存在");
        }
        KnowledgeArticle update = new KnowledgeArticle();
        update.setId(id);
        update.setStatus(status == null || status != 1 ? 0 : 1);
        articleMapper.updateById(update);
        return Result.success();
    }

    @Override
    public Result<ArticleVO> getArticle(Long id) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null) {
            throw new BizException("文章不存在");
        }
        return Result.success(toArticleVO(article));
    }

    // ==================== 前台 ====================

    @Override
    public Result<List<DirectoryVO>> frontDirectory() {
        List<KnowledgeDirectory> list = directoryMapper.selectList(
                Wrappers.<KnowledgeDirectory>lambdaQuery()
                        .orderByAsc(KnowledgeDirectory::getSort)
                        .orderByAsc(KnowledgeDirectory::getCreateTime));
        Map<Long, Long> countMap = articleMapper.selectList(
                        Wrappers.<KnowledgeArticle>lambdaQuery().eq(KnowledgeArticle::getStatus, 1)).stream()
                .collect(Collectors.groupingBy(KnowledgeArticle::getDirectoryId, Collectors.counting()));
        return Result.success(buildDirectoryTree(list, countMap));
    }

    @Override
    public Result<ArticleVO> frontArticleDetail(Long id) {
        KnowledgeArticle article = articleMapper.selectById(id);
        if (article == null || article.getStatus() == null || article.getStatus() != 1) {
            throw new BizException("文章不存在或未发布");
        }
        ArticleVO vo = toArticleVO(article);
        // 上下篇：同目录下按创建时间排序
        List<KnowledgeArticle> siblings = articleMapper.selectList(
                Wrappers.<KnowledgeArticle>lambdaQuery()
                        .eq(KnowledgeArticle::getDirectoryId, article.getDirectoryId())
                        .eq(KnowledgeArticle::getStatus, 1)
                        .orderByAsc(KnowledgeArticle::getCreateTime));
        for (int i = 0; i < siblings.size(); i++) {
            if (siblings.get(i).getId().equals(id)) {
                if (i > 0) {
                    vo.setPrevId(String.valueOf(siblings.get(i - 1).getId()));
                }
                if (i < siblings.size() - 1) {
                    vo.setNextId(String.valueOf(siblings.get(i + 1).getId()));
                }
                break;
            }
        }
        return Result.success(vo);
    }

    // ==================== 私有方法 ====================

    private List<DirectoryVO> buildDirectoryTree(List<KnowledgeDirectory> list, Map<Long, Long> countMap) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, DirectoryVO> map = new HashMap<>();
        for (KnowledgeDirectory d : list) {
            DirectoryVO vo = new DirectoryVO();
            vo.setId(String.valueOf(d.getId()));
            vo.setTitle(d.getTitle());
            vo.setParentId(d.getParentId() == null ? "0" : String.valueOf(d.getParentId()));
            vo.setSort(d.getSort());
            vo.setArticleCount(countMap.getOrDefault(d.getId(), 0L).intValue());
            vo.setCreateTime(d.getCreateTime());
            vo.setChildren(new ArrayList<>());
            map.put(d.getId(), vo);
        }
        List<DirectoryVO> roots = new ArrayList<>();
        for (KnowledgeDirectory d : list) {
            DirectoryVO vo = map.get(d.getId());
            if (d.getParentId() == null || d.getParentId() == 0L) {
                roots.add(vo);
            } else {
                DirectoryVO parent = map.get(d.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }
        sortTree(roots);
        return roots;
    }

    private void sortTree(List<DirectoryVO> list) {
        list.sort(Comparator.comparing(DirectoryVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        for (DirectoryVO vo : list) {
            if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
                sortTree(vo.getChildren());
            }
        }
    }

    private ArticleVO toArticleVO(KnowledgeArticle a) {
        ArticleVO vo = new ArticleVO();
        vo.setId(String.valueOf(a.getId()));
        vo.setTitle(a.getTitle());
        vo.setContent(a.getContent());
        vo.setContentHtml(a.getContentHtml());
        vo.setSummary(a.getSummary());
        vo.setDirectoryId(a.getDirectoryId() == null ? null : String.valueOf(a.getDirectoryId()));
        vo.setIsTop(a.getIsTop());
        vo.setStatus(a.getStatus());
        vo.setCreateTime(a.getCreateTime());
        vo.setUpdateTime(a.getUpdateTime());
        return vo;
    }
}
