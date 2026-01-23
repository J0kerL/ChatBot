package com.chatbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息实体类
 *
 * @Author Diamond
 * @Create 2026/1/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private Long conversationId;
    
    /**
     * 发送者类型（user:用户, ai:AI）
     */
    private SenderType senderType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 发送者类型枚举
     */
    public enum SenderType {
        USER("user", "用户"),
        AI("ai", "AI");
        
        private final String code;
        private final String desc;
        
        SenderType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDesc() {
            return desc;
        }
    }
}
