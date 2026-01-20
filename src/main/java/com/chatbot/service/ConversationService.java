package com.chatbot.service;

import com.chatbot.model.vo.ConversationVO;

import java.util.List;

/**
 * 会话服务
 * @author Administrator
 */
public interface ConversationService {

    /**
     * 创建会话
     */
    ConversationVO create(Long characterId);

    /**
     * 获取当前用户的会话列表
     */
    List<ConversationVO> list();

    /**
     * 删除会话
     */
    void delete(Long id);

    /**
     * 更新置顶状态
     *
     * @param isPinned 目标置顶状态；为空时表示切换
     */
    ConversationVO updatePinned(Long id, Boolean isPinned);

    /**
     * 标记会话已读（未读数归零）
     */
    ConversationVO markRead(Long id);
}
