package com.chatbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chatbot.common.exception.BizException;
import com.chatbot.common.util.BCryptUtil;
import com.chatbot.common.util.JwtUtil;
import com.chatbot.common.util.RedisUtil;
import com.chatbot.common.util.UserContext;
import com.chatbot.mapper.UserMapper;
import com.chatbot.model.dto.LoginDTO;
import com.chatbot.model.dto.RegisterDTO;
import com.chatbot.model.dto.UpdatePasswordDTO;
import com.chatbot.model.dto.UpdateUserInfoDTO;
import com.chatbot.model.entity.User;
import com.chatbot.model.vo.LoginVO;
import com.chatbot.model.vo.UserVO;
import com.chatbot.service.FileService;
import com.chatbot.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private FileService fileService;

    // Redis缓存相关常量
    private static final String USER_CACHE_PREFIX = "user:info:";
    // 过期时间2小时
    private static final long USER_CACHE_EXPIRE = 7200;

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
        user.setAvatar("https://chat-b0t.oss-cn-beijing.aliyuncs.com/avatar/defaultAvatar.png");
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());

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

            // 清除用户信息缓存
            clearUserCache(userId);

            log.info("用户退出登录：userId={}", userId);
        }
    }

    /**
     * 获取当前用户信息
     */
    @Override
    public UserVO getCurrentUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("用户未登录");
        }

        // 先从缓存获取
        User user = getUserFromCache(userId);

        // 缓存未命中，从数据库查询
        if (user == null) {
            user = userMapper.selectById(userId);
            if (user == null) {
                throw new BizException("用户不存在");
            }
            // 存入缓存
            cacheUser(user);
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        userVO.setPassword("******");
        return userVO;
    }

    /**
     * 修改用户信息
     */
    @Override
    public UserVO updateUserInfo(UpdateUserInfoDTO updateUserInfoDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("用户未登录");
        }

        User user = new User();
        user.setId(userId);
        user.setUsername(updateUserInfoDTO.getUsername());
        user.setEmail(updateUserInfoDTO.getEmail());

        int result = userMapper.updateUserInfo(user);
        if (result <= 0) {
            throw new BizException("修改用户信息失败");
        }

        // 清除缓存，让下次查询时重新加载
        clearUserCache(userId);

        log.info("修改用户信息成功：userId={}", userId);
        return getCurrentUserInfo();
    }

    /**
     * 修改密码
     */
    @Override
    public void updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        if (!BCryptUtil.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new BizException("旧密码错误");
        }

        String newPasswordEncoded = BCryptUtil.encode(updatePasswordDTO.getNewPassword());
        int result = userMapper.updatePassword(userId, newPasswordEncoded);
        if (result <= 0) {
            throw new BizException("修改密码失败");
        }

        // 密码修改后清除缓存
        clearUserCache(userId);

        log.info("修改密码成功：userId={}", userId);
    }

    /**
     * 上传用户头像
     */
    @Override
    public String updateAvatar(MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("用户未登录");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException("用户不存在");
        }

        // 校验文件类型（只允许图片）
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isImageFile(originalFilename)) {
            throw new BizException("只支持上传图片格式（jpg、png、gif、webp）");
        }

        // 上传新头像（使用专门的头像上传方法）
        String newAvatarUrl = fileService.uploadAvatar(file);

        // 删除旧头像（非默认头像才删除）
        String oldAvatar = user.getAvatar();
        if (oldAvatar != null && !oldAvatar.contains("defaultAvatar")) {
            try {
                fileService.deleteFile(oldAvatar);
                log.info("删除旧头像成功：{}", oldAvatar);
            } catch (Exception e) {
                log.warn("删除旧头像失败：{}，错误：{}", oldAvatar, e.getMessage());
            }
        }

        // 更新数据库
        int result = userMapper.updateAvatar(userId, newAvatarUrl);
        if (result <= 0) {
            throw new BizException("更新头像失败");
        }

        // 清除缓存，让下次查询时重新加载
        clearUserCache(userId);

        log.info("更新用户头像成功：userId={}, newAvatar={}", userId, newAvatarUrl);
        return newAvatarUrl;
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return extension.matches("jpg|jpeg|png|gif|bmp|webp");
    }

    /**
     * 从缓存中获取用户信息
     */
    private User getUserFromCache(Long userId) {
        String cacheKey = USER_CACHE_PREFIX + userId;
        Object cachedUser = redisUtil.get(cacheKey);
        if (cachedUser instanceof User) {
            log.debug("从缓存获取用户信息：userId={}", userId);
            return (User) cachedUser;
        }
        return null;
    }

    /**
     * 缓存用户信息
     */
    private void cacheUser(User user) {
        if (user != null) {
            String cacheKey = USER_CACHE_PREFIX + user.getId();
            redisUtil.set(cacheKey, user, USER_CACHE_EXPIRE);
            log.debug("从数据库查询并缓存用户信息：userId={}", user.getId());
        }
    }

    /**
     * 清除用户缓存
     */
    private void clearUserCache(Long userId) {
        if (userId != null) {
            String cacheKey = USER_CACHE_PREFIX + userId;
            redisUtil.delete(cacheKey);
            log.debug("清除用户缓存：userId={}", userId);
        }
    }
}
