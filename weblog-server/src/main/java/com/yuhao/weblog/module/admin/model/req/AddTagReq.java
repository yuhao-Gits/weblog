package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AddTagReq {

    @NotBlank(message = "标签名称不能为空")
    private String name;
}
