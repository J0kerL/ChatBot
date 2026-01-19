package com.chatbot.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI角色DTO
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
public class AiCharacterDTO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 所属用户ID
     */
    @NotNull(message = "所属用户ID不能为空")
    private Long userId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 身份关系（如：温柔女友、知心姐姐）
     */
    private String relationship;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 外貌描述
     */
    private String appearance;

    /**
     * 性格标签（JSON数组，如["温柔","体贴","幽默"]）
     */
    private List<String> personalityTags;

    /**
     * 背景故事
     */
    private String background;

    /**
     * 说话风格
     */
    private String speakingStyle;

    /**
     * 记忆设定（JSON格式，用于AI上下文记忆）
     */
    private String memorySettings;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}
