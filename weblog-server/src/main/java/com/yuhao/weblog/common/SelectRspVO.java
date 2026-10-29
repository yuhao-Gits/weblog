package com.yuhao.weblog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 下拉选择项（分类 / 标签 Select 列表通用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectRspVO implements Serializable {
    private Long value;
    private String label;
}
