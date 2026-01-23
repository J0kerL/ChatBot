package com.chatbot.model.dto;

import lombok.Data;

/**
 * 更新会话入参（动态更新：字段为空表示不更新）
 * @author Administrator
 */
@Data
public class UpdateConversationDTO {

    /**
     * 会话标题
     */
    private String title;

    /**
     * 是否置顶（0:否, 1:是）
     */
    private Boolean isPinned;
}
