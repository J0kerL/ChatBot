package com.chatbot.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 更新AI角色DTO
 * @Author Diamond
 * @Create 2026/1/16
 */
@Data
public class UpdateAiCharacterDTO {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色名称
     */
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
}
