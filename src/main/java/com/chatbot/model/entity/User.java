package com.chatbot.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户账号实体类
 * 
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
     * 头像地址
     */
    private String avatar;

    /**
     * 用户状态：1正常 0禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
