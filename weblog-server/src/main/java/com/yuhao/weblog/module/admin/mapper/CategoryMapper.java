package com.yuhao.weblog.module.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuhao.weblog.module.admin.model.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
