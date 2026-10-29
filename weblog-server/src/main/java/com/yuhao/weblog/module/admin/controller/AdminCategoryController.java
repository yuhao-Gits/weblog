package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.model.req.AddCategoryReq;
import com.yuhao.weblog.module.admin.model.req.QueryCategoryReq;
import com.yuhao.weblog.module.admin.model.vo.CategoryVO;
import com.yuhao.weblog.module.admin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<?> add(@RequestBody @Valid AddCategoryReq req) {
        return categoryService.addCategory(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("categoryId") Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    @PostMapping("/list")
    public Result<PageResult<CategoryVO>> list(@RequestBody QueryCategoryReq req) {
        return categoryService.listCategory(req);
    }

    @PostMapping("/select/list")
    public Result<List<SelectRspVO>> selectList() {
        return categoryService.selectCategory();
    }
}
