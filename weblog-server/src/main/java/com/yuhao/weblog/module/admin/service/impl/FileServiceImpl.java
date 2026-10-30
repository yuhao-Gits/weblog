package com.yuhao.weblog.module.admin.service.impl;

import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.storage.FileStorage;
import com.yuhao.weblog.module.admin.model.vo.UploadRspVO;
import com.yuhao.weblog.module.admin.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务实现。
 * <p>
 * 委托 {@link FileStorage} 完成实际存储（本地 / Minio 由 {@code weblog.storage.type}
 * 条件装配切换），Controller 与前端返回结构保持不变（仍返回 /files/xxx）。
 * </p>
 *
 * @author weblog
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileStorage fileStorage;

    @Override
    public Result<UploadRspVO> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("文件为空");
        }
        try {
            String url = fileStorage.store(file);
            log.info("文件上传成功：{} -> {}", file.getOriginalFilename(), url);
            return Result.success(new UploadRspVO(url));
        } catch (BizException e) {
            // 业务异常（含存储层抛出）直接向上传递，保留原始提示
            throw e;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }
}
