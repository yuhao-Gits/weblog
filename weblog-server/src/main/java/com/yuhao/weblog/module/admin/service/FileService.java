package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.UploadRspVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件（默认本地磁盘，生产可切换 Minio）
     */
    Result<UploadRspVO> upload(MultipartFile file);
}
