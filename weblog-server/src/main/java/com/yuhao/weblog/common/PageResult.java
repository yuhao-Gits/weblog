package com.yuhao.weblog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    /** 总记录数 */
    private Long total;
    /** 当前页数据 */
    private List<T> records;
    /** 当前页码 */
    private Long current;
    /** 每页条数 */
    private Long size;

    public static <T> PageResult<T> success(List<T> records, long total, long current, long size) {
        return new PageResult<>(total, records, current, size);
    }

    /** 便捷构造：仅传数据与总数（分页参数默认 1/records.size） */
    public static <T> PageResult<T> of(List<T> records, long total) {
        long size = records == null ? 0 : records.size();
        return new PageResult<>(total, records, 1L, size);
    }
}
