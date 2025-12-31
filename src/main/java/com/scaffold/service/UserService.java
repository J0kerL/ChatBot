package com.scaffold.service;

import com.scaffold.model.dto.LoginDTO;
import com.scaffold.model.dto.RegisterDTO;
import com.scaffold.model.vo.LoginVO;
import com.scaffold.model.vo.UserVO;

/**
 * 用户账号服务接口
 * @author Diamond
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserVO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户退出登录
     */
    void logout();
}
