package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.UpdateBlogSettingsReq;
import com.yuhao.weblog.module.admin.model.vo.BlogSettingsVO;
import com.yuhao.weblog.module.admin.service.BlogSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/blog/settings")
@RequiredArgsConstructor
public class AdminBlogSettingsController {

    private final BlogSettingsService blogSettingsService;

    @GetMapping("/detail")
    public Result<BlogSettingsVO> detail() {
        return blogSettingsService.findDetail();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    public Result<?> update(@RequestBody UpdateBlogSettingsReq req) {
        return blogSettingsService.update(req);
    }
}
