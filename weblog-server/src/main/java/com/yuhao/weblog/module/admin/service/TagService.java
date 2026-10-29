package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.model.req.AddTagReq;
import com.yuhao.weblog.module.admin.model.req.QueryTagReq;
import com.yuhao.weblog.module.admin.model.vo.TagVO;

import java.util.List;

public interface TagService {

    Result<?> addTag(AddTagReq req);

    Result<?> deleteTag(Long tagId);

    Result<PageResult<TagVO>> listTag(QueryTagReq req);

    Result<List<SelectRspVO>> selectTag();
}
