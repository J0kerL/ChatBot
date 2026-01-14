package com.chatbot.mapper;

import com.chatbot.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户账号Mapper接口
 *
 * @author Diamond
 */
@Mapper
public interface UserMapper {

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 根据ID查询
     */
    User selectById(Long id);

    /**
     * 根据用户名查询
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 更新用户信息
     */
    int updateUserInfo(User user);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新用户头像
     */
    int updateAvatar(@Param("id") Long userId, @Param("avatar") String newAvatarUrl);
}
