package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.PageResult;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.SelectRspVO;
import com.yuhao.weblog.module.admin.model.req.AddTagReq;
import com.yuhao.weblog.module.admin.model.req.QueryTagReq;
import com.yuhao.weblog.module.admin.model.vo.TagVO;
import com.yuhao.weblog.module.admin.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/tag")
@RequiredArgsConstructor
public class AdminTagController {

    private final TagService tagService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public Result<?> add(@RequestBody @Valid AddTagReq req) {
        return tagService.addTag(req);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete")
    public Result<?> delete(@RequestParam("tagId") Long tagId) {
        return tagService.deleteTag(tagId);
    }

    @PostMapping("/list")
    public Result<PageResult<TagVO>> list(@RequestBody QueryTagReq req) {
        return tagService.listTag(req);
    }

    @PostMapping("/select/list")
    public Result<List<SelectRspVO>> selectList() {
        return tagService.selectTag();
    }
}
