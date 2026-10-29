package com.yuhao.weblog.module.admin.service.impl;

import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.vo.UploadRspVO;
import com.yuhao.weblog.module.admin.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${weblog.upload.path:./uploads}")
    private String uploadPath;

    @Value("${weblog.upload.url-prefix:/files}")
    private String urlPrefix;

    @Override
    public Result<UploadRspVO> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("文件为空");
        }
        try {
            // 统一使用绝对路径，避免相对路径在 Tomcat 工作目录下不一致
            File dir = new File(uploadPath).getAbsoluteFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newName = UUID.randomUUID().toString().replace("-", "") + ext;
            File dest = new File(dir, newName);
            file.transferTo(dest);

            String url = urlPrefix + "/" + newName;
            log.info("文件上传成功：{} -> {}", originalName, dest.getAbsolutePath());
            return Result.success(new UploadRspVO(url));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }
}
