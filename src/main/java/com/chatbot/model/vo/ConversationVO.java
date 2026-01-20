package com.chatbot.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会话返回数据
 * @author Administrator
 */
@Data
public class ConversationVO {

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
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 是否置顶
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
