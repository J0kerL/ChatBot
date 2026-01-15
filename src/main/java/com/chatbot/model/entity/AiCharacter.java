package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI角色实体类
 *
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCharacter {
    
    /**
     * 角色ID
     */
    private Long id;
    
    /**
     * 所属用户ID
     */
    private Long userId;
    
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
    
    /**
     * 是否为预设模板（0:否, 1:是）
     */
    private Boolean isTemplate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
