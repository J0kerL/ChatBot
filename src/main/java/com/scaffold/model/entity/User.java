package com.scaffold.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户账号实体类
 * @author Diamond
 */
@Data
public class User {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt）
     */
    private String password;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
