package com.chatbot.service;

import com.chatbot.common.result.PageResult;
import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.dto.UpdateAiCharacterDTO;
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

    /**
     * 分页查询AI角色
     */
    PageResult page(AiCharacterPageQueryDTO aiCharacterPageQueryDTO);

    /**
     * 查看AI角色详情
     *
     * @param id AI角色ID
     * @return AI角色VO
     */
    AiCharacterVO getById(Long id);

    /**
     * 更新AI角色信息
     *
     * @param updateAiCharacterDTO 更新DTO
     * @return 更新后的AI角色VO
     */
    AiCharacterVO update(@Valid UpdateAiCharacterDTO updateAiCharacterDTO);

    /**
     * 删除AI角色
     */
    void delete(Long id);
}
