package com.chatbot.mapper;

import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.dto.UpdateAiCharacterDTO;
import com.chatbot.model.entity.AiCharacter;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Mapper
public interface AiCharacterMapper {

    /**
     * 插入AI角色
     *
     * @param aiCharacterDTO AI角色DTO
     * @return 受影响的行数
     */
    int insert(AiCharacterDTO aiCharacterDTO);

    /**
     * 根据ID查询AI角色
     *
     * @param id AI角色ID
     * @return AI角色实体
     */
    AiCharacter selectById(Long id);

    /**
     * 分页查询AI角色
     *
     * @param aiCharacterPageQueryDTO 分页查询条件
     * @return AI角色列表
     */
    Page<AiCharacter> query(AiCharacterPageQueryDTO aiCharacterPageQueryDTO);

    /**
     * 动态更新AI角色信息
     *
     * @param updateAiCharacterDTO 更新DTO
     * @return 受影响的行数
     */
    int updateSelective(UpdateAiCharacterDTO updateAiCharacterDTO);

    /**
     * 根据ID删除AI角色
     *
     * @param id AI角色ID
     * @return 受影响的行数
     */
    @Delete("DELETE FROM ai_character WHERE id = #{id}")
    int deleteById(Long id);
}
