package com.chatbot.mapper;

import org.apache.ibatis.annotations.Delete;

/**
 * @Author Diamond
 * @Create 2026/1/16
 */
public interface MessageMapper {
    @Delete("DELETE FROM message WHERE conversation_id = #{id}")
    void deleteByConversationId(Long id);
}
