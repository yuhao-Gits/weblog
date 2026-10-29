package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.UploadRspVO;
import com.yuhao.weblog.module.admin.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/file")
@RequiredArgsConstructor
public class AdminFileController {

    private final FileService fileService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public Result<UploadRspVO> upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }
}
