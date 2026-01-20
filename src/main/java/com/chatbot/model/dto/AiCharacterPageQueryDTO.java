package com.chatbot.model.dto;

import lombok.Data;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
public class AiCharacterPageQueryDTO {

    private Long userId;

    private String name;

    // 页码
    private int pageNum;

    // 每页显示记录数
    private int pageSize;

}
