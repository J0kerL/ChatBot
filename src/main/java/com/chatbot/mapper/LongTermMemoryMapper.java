package com.chatbot.mapper;

import org.apache.ibatis.annotations.Delete;

/**
 * @Author Diamond
 * @Create 2026/1/16
 */
public interface LongTermMemoryMapper {

    /**
     * 根据角色ID删除长期记忆
     * @param id
     */
    @Delete("delete from long_term_memory where character_id = #{id}")
    void deleteByCharacterId(Long id);

}
