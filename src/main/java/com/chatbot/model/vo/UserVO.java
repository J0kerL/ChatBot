package com.chatbot.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Diamond
 * @create 2025-12-09 17:37
 */
@Data
public class UserVO {

    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;

}
