package com.chatbot.mapper;

import com.chatbot.model.entity.Conversation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会话表数据访问接口
 * @author Administrator
 */
public interface ConversationMapper {

    /**
     * 新增会话（自增主键回填到 conversation.id）
     */
    int insert(Conversation conversation);

    /**
     * 根据会话ID查询
     */
    Conversation selectById(Long id);

    /**
     * 根据用户ID查询会话列表（具体排序由XML实现）
     */
    List<Conversation> selectByUserId(Long userId);

    /**
     * 动态更新（按 id + user_id 约束，防止越权修改）
     */
    int updateSelective(Conversation conversation);

    /**
     * 更新置顶状态（按 id + user_id 约束）
     */
    int updatePinned(@Param("id") Long id, @Param("userId") Long userId, @Param("isPinned") Boolean isPinned);

    /**
     * 更新未读数（按 id + user_id 约束）
     */
    int updateUnreadCount(@Param("id") Long id, @Param("userId") Long userId, @Param("unreadCount") Integer unreadCount);

    /**
     * 删除会话（按 id + user_id 约束）
     */
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 根据角色ID查询所有对话ID列表（用于删除角色时的级联清理）
     */
    @Select("select id from conversation where character_id = #{characterId}")
    List<Long> selectIdsByCharacterId(Long characterId);

    /**
     * 根据角色ID删除会话记录（用于删除角色时的级联清理）
     */
    @Delete("delete from conversation where character_id = #{id}")
    void deleteByCharacterId(Long id);
}
