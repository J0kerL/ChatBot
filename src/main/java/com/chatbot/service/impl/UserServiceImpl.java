package com.chatbot.service.impl;

import com.chatbot.common.exception.BizException;
import com.chatbot.common.util.BCryptUtil;
import com.chatbot.common.util.JwtUtil;
import com.chatbot.common.util.RedisUtil;
import com.chatbot.common.util.UserContext;
import com.chatbot.mapper.UserMapper;
import com.chatbot.model.dto.LoginDTO;
import com.chatbot.model.dto.RegisterDTO;
import com.chatbot.model.entity.User;
import com.chatbot.model.vo.LoginVO;
import com.chatbot.model.vo.UserVO;
import com.chatbot.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户账号服务实现
 *
 * @author Diamond
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 用户注册
     */
    @Override
    public UserVO register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new BizException("用户名已存在");
        }
        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(BCryptUtil.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BizException("注册失败");
        }
        log.info("用户注册成功：username={}", registerDTO.getUsername());
        User account = userMapper.selectByUsername(registerDTO.getUsername());
        UserVO userVO = new UserVO();
        userVO.setId(account.getId());
        userVO.setUsername(account.getUsername());
        userVO.setPassword("******");
        userVO.setEmail(account.getEmail());
        userVO.setAvatar(account.getAvatar());
        userVO.setStatus(account.getStatus());
        userVO.setCreatedAt(account.getCreatedAt());
        userVO.setUpdatedAt(account.getUpdatedAt());
        return userVO;
    }

    /**
     * 用户登录
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BizException("该用户不存在");
        }

        // 验证密码
        if (!BCryptUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 将Token存入Redis（有效期与JWT Token一致）
        String redisKey = "token:" + user.getId();
        redisUtil.set(redisKey, token, jwtUtil.getExpiration() / 1000);

        log.info("用户登录成功：userId={}, username={}", user.getId(), user.getUsername());

        // 返回登录信息
        return new LoginVO(token, user.getId(), user.getUsername());
    }

    /**
     * 用户退出登录
     */
    @Override
    public void logout() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            // 从Redis中删除Token
            String redisKey = "token:" + userId;
            redisUtil.delete(redisKey);
            log.info("用户退出登录：userId={}", userId);
        }
    }
}
