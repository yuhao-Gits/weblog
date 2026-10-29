package com.yuhao.weblog.module.admin.service;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.LoginReq;
import com.yuhao.weblog.module.admin.model.req.UpdatePasswordReq;
import com.yuhao.weblog.module.admin.model.vo.LoginRspVO;
import com.yuhao.weblog.module.admin.model.vo.UserInfoVO;

public interface AuthService {

    /**
     * 登录
     */
    Result<LoginRspVO> login(LoginReq req);

    /**
     * 获取当前登录用户信息
     */
    Result<UserInfoVO> userInfo();

    /**
     * 修改密码
     */
    Result<Boolean> updatePassword(UpdatePasswordReq req);
}
