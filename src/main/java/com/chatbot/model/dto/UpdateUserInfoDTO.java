package com.chatbot.model.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * 修改用户信息DTO
 * @author Diamond
 */
@Data
public class UpdateUserInfoDTO {

    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;
}
