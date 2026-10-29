package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.mapper.ArticleTagRelMapper;
import com.yuhao.weblog.module.admin.mapper.TagMapper;
import com.yuhao.weblog.module.admin.model.entity.Tag;
import com.yuhao.weblog.module.admin.model.req.AddTagReq;
import com.yuhao.weblog.module.admin.model.req.QueryTagReq;
import com.yuhao.weblog.module.admin.model.vo.TagVO;
import com.yuhao.weblog.module.admin.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagRelMapper articleTagRelMapper;

    @Override
    public Result<?> addTag(AddTagReq req) {
        Long count = tagMapper.selectCount(Wrappers.<Tag>lambdaQuery().eq(Tag::getName, req.getName().trim()));
        if (count != null && count > 0) {
            throw new BizException("标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(req.getName().trim());
        tagMapper.insert(tag);
        return Result.success();
    }

    @Override
    public Result<?> deleteTag(Long tagId) {
        Long relCount = articleTagRelMapper.selectCount(
                Wrappers.<com.yuhao.weblog.module.admin.model.entity.ArticleTagRel>lambdaQuery()
                        .eq(com.yuhao.weblog.module.admin.model.entity.ArticleTagRel::getTagId, tagId));
        if (relCount != null && relCount > 0) {
            throw new BizException("该标签下存在文章，无法删除");
        }
        tagMapper.deleteById(tagId);
        return Result.success();
    }

    @Override
    public Result<PageResult<TagVO>> listTag(QueryTagReq req) {
        Page<Tag> page = tagMapper.selectPage(
                new Page<>(req.getCurrent(), req.getSize()),
                Wrappers.<Tag>lambdaQuery()
                        .like(req.getName() != null, Tag::getName, req.getName())
                        .orderByDesc(Tag::getCreateTime));
        List<TagVO> vos = page.getRecords().stream().map(t -> {
            TagVO vo = new TagVO();
            vo.setId(t.getId());
            vo.setName(t.getName());
            vo.setCreateTime(t.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
        return Result.success(PageResult.of(vos, page.getTotal()));
    }

    @Override
    public Result<List<SelectRspVO>> selectTag() {
        List<SelectRspVO> list = tagMapper.selectList(
                Wrappers.<Tag>lambdaQuery().orderByDesc(Tag::getCreateTime))
                .stream().map(t -> new SelectRspVO(t.getId(), t.getName()))
                .collect(Collectors.toList());
        return Result.success(list);
    }
}
