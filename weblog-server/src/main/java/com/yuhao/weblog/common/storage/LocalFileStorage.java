package com.yuhao.weblog.common.storage;

import com.yuhao.weblog.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 本地磁盘文件存储实现（开发环境默认）。
 * <p>
 * 由原 {@code FileServiceImpl} 的磁盘逻辑迁移而来：对象写入 {@code weblog.upload.path}
 * 目录，访问 URL 使用 {@code weblog.upload.url-prefix}（默认 /files）拼接。
 * 仅当 {@code weblog.storage.type=local}（或缺省）时装配。
 * </p>
 *
 * @author weblog
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "weblog.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    /** 本地上传根目录（支持相对路径，运行时解析为绝对路径） */
    @Value("${weblog.upload.path:./uploads}")
    private String uploadPath;

    /** 对外访问前缀（前端拼接，默认 /files） */
    @Value("${weblog.upload.url-prefix:/files}")
    private String urlPrefix;

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("文件为空");
        }
        try {
            // 统一使用绝对路径，避免相对路径在 Tomcat 工作目录下不一致
            File dir = new File(uploadPath).getAbsoluteFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("无法创建上传目录：" + dir.getAbsolutePath());
            }

            String objectName = generateObjectName(file.getOriginalFilename());
            File dest = new File(dir, objectName);
            file.transferTo(dest);

            String url = buildUrl(objectName);
            log.info("本地文件上传成功：{} -> {}", file.getOriginalFilename(), dest.getAbsolutePath());
            return url;
        } catch (IOException e) {
            log.error("本地文件上传失败", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public void delete(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return;
        }
        // 仅删除纯文件名，防路径穿越
        String safeName = sanitizeObjectName(objectName);
        File dir = new File(uploadPath).getAbsoluteFile();
        File target = new File(dir, safeName);
        // 确保目标文件确实位于上传目录内
        if (target.getParentFile() == null
                || !target.getParentFile().getAbsolutePath().equals(dir.getAbsolutePath())) {
            log.warn("拒绝删除上传目录之外的文件：{}", objectName);
            return;
        }
        if (target.exists() && target.isFile() && !target.delete()) {
            log.warn("本地文件删除失败：{}", target.getAbsolutePath());
        }
    }

    @Override
    public String buildUrl(String objectName) {
        return urlPrefix + "/" + objectName;
    }

    /**
     * 生成对象名：UUID（去横线）+ 原扩展名（小写）。
     */
    private String generateObjectName(String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }

    /**
     * 过滤对象名，只保留最后一段文件名，防止 ../ 路径穿越。
     */
    private String sanitizeObjectName(String objectName) {
        String name = objectName.replace("\\", "/");
        int idx = name.lastIndexOf('/');
        return idx >= 0 ? name.substring(idx + 1) : name;
    }
}
