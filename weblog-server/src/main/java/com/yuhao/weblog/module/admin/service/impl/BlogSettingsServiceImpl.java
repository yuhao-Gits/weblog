package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.mapper.BlogSettingsMapper;
import com.yuhao.weblog.module.admin.model.entity.BlogSettings;
import com.yuhao.weblog.module.admin.model.req.UpdateBlogSettingsReq;
import com.yuhao.weblog.module.admin.model.vo.BlogSettingsVO;
import com.yuhao.weblog.module.admin.service.BlogSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogSettingsServiceImpl implements BlogSettingsService {

    private final BlogSettingsMapper blogSettingsMapper;

    @Override
    public Result<BlogSettingsVO> findDetail() {
        BlogSettings entity = blogSettingsMapper.selectOne(
                Wrappers.<BlogSettings>lambdaQuery().eq(BlogSettings::getId, 1L));
        BlogSettingsVO vo = new BlogSettingsVO();
        if (entity != null) {
            BeanUtils.copyProperties(entity, vo);
        }
        return Result.success(vo);
    }

    @Override
    public Result<?> update(UpdateBlogSettingsReq req) {
        BlogSettings entity = blogSettingsMapper.selectOne(
                Wrappers.<BlogSettings>lambdaQuery().eq(BlogSettings::getId, 1L));
        if (entity == null) {
            entity = new BlogSettings();
            entity.setId(1L);
        }
        entity.setBlogName(req.getBlogName());
        entity.setAuthorName(req.getAuthorName());
        entity.setIntroduction(req.getIntroduction());
        entity.setAvatar(req.getAvatar());
        entity.setLogo(req.getLogo());
        entity.setGithubHome(req.getGithubHome());
        entity.setGiteeHome(req.getGiteeHome());
        entity.setWechatQrCode(req.getWechatQrCode());
        entity.setQqQrCode(req.getQqQrCode());
        entity.setSensitiveWords(req.getSensitiveWords());
        entity.setCommentAudit(req.getCommentAudit());
        entity.setEmailHost(req.getEmailHost());
        entity.setEmailPort(req.getEmailPort());
        entity.setEmailUsername(req.getEmailUsername());
        if (req.getEmailPassword() != null) {
            entity.setEmailPassword(req.getEmailPassword());
        }
        entity.setEmailFrom(req.getEmailFrom());

        if (entity.getId() != null && blogSettingsMapper.selectById(1L) != null) {
            blogSettingsMapper.updateById(entity);
        } else {
            blogSettingsMapper.insert(entity);
        }
        return Result.success();
    }
}
