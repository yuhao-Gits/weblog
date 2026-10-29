package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddCategoryReq {

    @NotBlank(message = "分类名称不能为空")
    private String name;
}
