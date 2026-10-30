package com.yuhao.weblog.common.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储抽象接口。
 * <p>
 * 定义「存储 / 删除 / 构造访问 URL」三个能力，本地磁盘（开发）与 Minio（生产）
 * 通过实现类切换，Controller 与 Service 层无感知。
 * </p>
 *
 * @author weblog
 */
public interface FileStorage {

    /**
     * 存储文件，并返回对外访问路径（统一以 /files 前缀开头，如 /files/uuid.ext）。
     *
     * @param file 上传的多部分文件，不允许为空
     * @return 对外访问路径，例如 /files/3f2ab1c0d4e5.jpg
     */
    String store(MultipartFile file);

    /**
     * 删除指定对象。
     *
     * @param objectName 对象名（不含访问前缀，如 3f2ab1c0d4e5.jpg）
     */
    void delete(String objectName);

    /**
     * 根据对象名构造对外访问 URL。
     *
     * @param objectName 对象名（不含访问前缀）
     * @return 对外访问 URL，例如 /files/3f2ab1c0d4e5.jpg
     */
    String buildUrl(String objectName);
}
