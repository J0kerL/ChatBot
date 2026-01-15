package com.chatbot.service;

import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.vo.AiCharacterVO;
import jakarta.validation.Valid;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
public interface AiCharacterService {

    /**
     * 添加AI角色
     */
    AiCharacterVO add(@Valid AiCharacterDTO aiCharacterDTO);

}
