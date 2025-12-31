package com.scaffold.mapper;

import com.scaffold.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户账号Mapper接口
 * @author Diamond
 */
@Mapper
public interface UserMapper {

    /**
     * 插入用户
     */
    int insert(User user);

    /**
     * 根据ID删除
     */
    int deleteById(Long id);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 根据ID查询
     */
    User selectById(Long id);

    /**
     * 根据用户名查询
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 查询所有用户
     */
    List<User> selectAll();
}
