package com.chatbot.mapper;

import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.entity.AiCharacter;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Mapper
public interface AiCharacterMapper {

    int insert(AiCharacterDTO aiCharacterDTO);

    @Select("SELECT * FROM ai_character WHERE id = #{id}")
    AiCharacter selectById(Long id);

    Page<AiCharacter> query(AiCharacterPageQueryDTO aiCharacterPageQueryDTO);
}
