package com.chatbot.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author Diamond
 * @Create 2026/1/16
 */
public interface ConversationMapper {

    /**
     * 根据角色ID查询所有对话ID列表
     *
     * @param characterId 角色ID
     * @return 对话ID列表
     */
    @Select("select id from conversation where character_id = #{characterId}")
    List<Long> selectIdsByCharacterId(Long characterId);

    /**
     * 根据角色ID删除对话记录
     *
     * @param id
     */
    @Delete("delete from conversation where character_id = #{id}")
    void deleteByCharacterId(Long id);
}
