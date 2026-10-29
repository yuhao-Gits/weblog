package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.UpdateBlogSettingsReq;
import com.yuhao.weblog.module.admin.model.vo.BlogSettingsVO;

public interface BlogSettingsService {

    Result<BlogSettingsVO> findDetail();

    Result<?> update(UpdateBlogSettingsReq req);
}
