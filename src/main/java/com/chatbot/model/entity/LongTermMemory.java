package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 长期记忆实体类
 *
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LongTermMemory {
    
    /**
     * 记忆ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * AI角色ID
     */
    private Long characterId;
    
    /**
     * 记忆关键词（如：用户喜好、重要事件）
     */
    private String memoryKey;
    
    /**
     * 记忆内容
     */
    private String memoryContent;
    
    /**
     * 重要性等级（1-5，5为最重要）
     */
    private Integer importance;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
