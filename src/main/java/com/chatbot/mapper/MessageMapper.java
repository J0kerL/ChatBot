package com.chatbot.mapper;

import com.chatbot.model.entity.Message;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author Diamond
 * @Create 2026/1/16
 */
public interface MessageMapper {

    int insert(Message message);

    Page<Message> pageByConversationId(@Param("conversationId") Long conversationId);

    List<Message> selectRecentByConversationId(@Param("conversationId") Long conversationId, @Param("limit") Integer limit);

    @Delete("DELETE FROM message WHERE conversation_id = #{id}")
    void deleteByConversationId(Long id);

    /**
     * 查询会话的最后一条消息
     */
    Message selectLastByConversationId(@Param("conversationId") Long conversationId);
}
