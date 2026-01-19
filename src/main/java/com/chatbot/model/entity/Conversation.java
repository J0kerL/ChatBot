package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话实体类
 *
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    
    /**
     * 会话ID
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
     * 会话标题（可自动生成或用户编辑）
     */
    private String title;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
    
    /**
     * 是否置顶（0:否, 1:是）
     */
    private Boolean isPinned;
    
    /**
     * 最后一条消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
