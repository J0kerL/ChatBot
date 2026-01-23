package com.chatbot.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageVO {

    private Long id;

    private Long conversationId;

    private String senderType;

    private String content;

    private LocalDateTime createdAt;
}

