package com.yuhao.weblog.common.storage;

import com.yuhao.weblog.common.BizException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * Minio 对象存储实现（生产环境默认）。
 * <p>
 * 将上传文件以流式写入 bucket，对象名 = UUID（去横线）+ 原扩展名；
 * 访问 URL 统一返回 /files 前缀，由 Nginx 直连 Minio 匿名下载，不经后端。
 * 仅当 {@code weblog.storage.type=minio} 时装配。
 * </p>
 *
 * @author weblog
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "weblog.storage.type", havingValue = "minio")
public class MinioFileStorage implements FileStorage {

    private final MinioClient minioClient;

    /** bucket 名（与 minio-init 建桶保持一致） */
    @Value("${weblog.minio.bucket:weblog}")
    private String bucket;

    /** 对外访问前缀（前端拼接，默认 /files） */
    @Value("${weblog.upload.url-prefix:/files}")
    private String urlPrefix;

    public MinioFileStorage(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BizException("文件为空");
        }
        String objectName = generateObjectName(file.getOriginalFilename());
        String contentType = resolveContentType(file);
        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            // 流式上传：objectSize 传实际大小，partSize=-1 由 SDK 决定分片
                            .stream(in, file.getSize(), -1)
                            .contentType(contentType)
                            .build());
            String url = buildUrl(objectName);
            log.info("Minio 文件上传成功：{} -> {}/{}", file.getOriginalFilename(), bucket, objectName);
            return url;
        } catch (Exception e) {
            log.error("Minio 文件上传失败", e);
            throw new BizException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public void delete(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return;
        }
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.warn("Minio 对象删除失败：{}/{}，忽略", bucket, objectName, e);
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
     * 解析 Content-Type，避免 null 导致 Minio SDK 报错。
     */
    private String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType == null || contentType.isEmpty()
                ? "application/octet-stream"
                : contentType;
    }
}
