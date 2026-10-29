package com.yuhao.weblog.module.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tag")
public class Tag extends BaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 标签名称 */
    private String name;
}
