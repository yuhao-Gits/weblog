package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.model.req.AddCategoryReq;
import com.yuhao.weblog.module.admin.model.req.QueryCategoryReq;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    Result<?> addCategory(AddCategoryReq req);

    Result<?> deleteCategory(Long categoryId);

    Result<PageResult<CategoryVO>> listCategory(QueryCategoryReq req);

    Result<List<SelectRspVO>> selectCategory();
}
