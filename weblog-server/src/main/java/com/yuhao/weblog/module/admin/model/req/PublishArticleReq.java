package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PublishArticleReq {

    /** 文章 ID：为空=新增，有值=更新 */
    private Long id;

    @NotBlank(message = "文章标题不能为空")
    private String title;

    /** 封面图 URL（可选） */
    private String cover;

    /** 摘要（可选，为空则自动生成） */
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @NotNull(message = "请选择分类")
    private Long categoryId;

    /** 标签 ID 列表 */
    private List<Long> tagIds;

    /** 是否置顶：1-是 0-否 */
    private Integer isTop;
}
