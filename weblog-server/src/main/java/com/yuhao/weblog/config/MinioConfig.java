package com.yuhao.weblog.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio 客户端条件装配。
 * <p>
 * 仅当 {@code weblog.storage.type=minio} 时创建 {@link MinioClient} Bean，
 * 供 {@code MinioFileStorage} 注入使用；本地存储模式下不装配，避免无效连接。
 * </p>
 *
 * @author weblog
 */
@Configuration
@ConditionalOnProperty(name = "weblog.storage.type", havingValue = "minio")
public class MinioConfig {

    @Value("${weblog.minio.endpoint:http://minio:9000}")
    private String endpoint;

    @Value("${weblog.minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${weblog.minio.secret-key:minioadmin}")
    private String secretKey;

    /**
     * 构建 MinioClient。
     *
     * @return 已配置 endpoint 与凭据的 MinioClient 实例
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
