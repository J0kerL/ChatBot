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
     * 消息内容（文本或文件地址）
     */
    private String content;
    
    /**
     * 消息类型
     */
    private MessageType messageType;
    
    /**
     * 文件地址（图片、语音等）
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Integer fileSize;
    
    /**
     * 语音消息时长（秒）
     */
    private Integer duration;
    
    /**
     * 是否已读（仅对AI消息有效，0:未读, 1:已读）
     */
    private Boolean isRead;
    
    /**
     * 是否已撤回（0:否, 1:是）
     */
    private Boolean isWithdrawn;
    
    /**
     * 撤回时间
     */
    private LocalDateTime withdrawnAt;
    
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
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        TEXT("text", "文本"),
        IMAGE("image", "图片"),
        VOICE("voice", "语音"),
        EMOJI("emoji", "表情"),
        SYSTEM("system", "系统消息");
        
        private final String code;
        private final String desc;
        
        MessageType(String code, String desc) {
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
