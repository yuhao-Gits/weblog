package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.mapper.ArticleMapper;
import com.yuhao.weblog.module.admin.mapper.CategoryMapper;
import com.yuhao.weblog.module.admin.model.entity.Category;
import com.yuhao.weblog.module.admin.model.req.AddCategoryReq;
import com.yuhao.weblog.module.admin.model.req.QueryCategoryReq;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;
import com.yuhao.weblog.module.admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public Result<?> addCategory(AddCategoryReq req) {
        Long count = categoryMapper.selectCount(
                Wrappers.<Category>lambdaQuery().eq(Category::getName, req.getName().trim()));
        if (count != null && count > 0) {
            throw new BizException("分类已存在");
        }
        Category category = new Category();
        category.setName(req.getName().trim());
        categoryMapper.insert(category);
        return Result.success();
    }

    @Override
    public Result<?> deleteCategory(Long categoryId) {
        Long articleCount = articleMapper.selectCount(
                Wrappers.<com.yuhao.weblog.module.admin.model.entity.Article>lambdaQuery()
                        .eq(com.yuhao.weblog.module.admin.model.entity.Article::getCategoryId, categoryId));
        if (articleCount != null && articleCount > 0) {
            throw new BizException("该分类下存在文章，无法删除");
        }
        categoryMapper.deleteById(categoryId);
        return Result.success();
    }

    @Override
    public Result<PageResult<CategoryVO>> listCategory(QueryCategoryReq req) {
        Page<Category> page = categoryMapper.selectPage(
                new Page<>(req.getCurrent(), req.getSize()),
                Wrappers.<Category>lambdaQuery()
                        .like(req.getName() != null, Category::getName, req.getName())
                        .orderByDesc(Category::getCreateTime));

        List<CategoryVO> vos = page.getRecords().stream().map(c -> {
            CategoryVO vo = new CategoryVO();
            vo.setId(c.getId());
            vo.setName(c.getName());
            vo.setCreateTime(c.getCreateTime());
            Long total = articleMapper.selectCount(
                    Wrappers.<com.yuhao.weblog.module.admin.model.entity.Article>lambdaQuery()
                            .eq(com.yuhao.weblog.module.admin.model.entity.Article::getCategoryId, c.getId()));
            vo.setArticlesTotal(total == null ? 0L : total);
            return vo;
        }).collect(Collectors.toList());

        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    public Result<List<SelectRspVO>> selectCategory() {
        List<SelectRspVO> list = categoryMapper.selectList(
                Wrappers.<Category>lambdaQuery().orderByDesc(Category::getCreateTime))
                .stream().map(c -> new SelectRspVO(c.getId(), c.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
}
