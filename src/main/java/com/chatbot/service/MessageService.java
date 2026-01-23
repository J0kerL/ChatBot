package com.chatbot.service;

import com.chatbot.common.result.PageResult;
import com.chatbot.model.dto.SendMessageDTO;
import com.chatbot.model.vo.MessageVO;

/**
 * @author Administrator
 */
public interface MessageService {

    MessageVO send(SendMessageDTO sendMessageDTO);

    PageResult list(Long conversationId, int pageNum, int pageSize);
}

