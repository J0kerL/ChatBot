package com.chatbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chatbot.common.exception.BizException;
import com.chatbot.common.util.UserContext;
import com.chatbot.mapper.AiCharacterMapper;
import com.chatbot.mapper.ConversationMapper;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.entity.Conversation;
import com.chatbot.model.vo.ConversationVO;
import com.chatbot.service.ConversationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话服务实现
 *
 * <p>统一通过 {@link UserContext#getUserId()} 做用户隔离与权限校验。</p>
 * @author Administrator
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    @Resource
    private ConversationMapper conversationMapper;
    @Resource
    private AiCharacterMapper aiCharacterMapper;
    @Resource
    private com.chatbot.mapper.MessageMapper messageMapper;

    /**
     * 创建会话
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ConversationVO create(Long characterId) {
        Long userId = requireUserId();
        AiCharacter aiCharacter = aiCharacterMapper.selectById(characterId);
        if (aiCharacter == null) {
            throw new BizException("AI角色不存在");
        }
        if (!userId.equals(aiCharacter.getUserId())) {
            throw new BizException("与该用户不是好友关系");
        }
        LocalDateTime now = LocalDateTime.now();
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setCharacterId(characterId);
        conversation.setIsPinned(false);
        conversation.setLastMessageTime(now);
        conversation.setCreatedAt(now);
        conversation.setUpdatedAt(now);
        int result = conversationMapper.insert(conversation);
        if (result <= 0) {
            throw new BizException("创建会话失败");
        }
        Conversation saved = conversationMapper.selectById(conversation.getId());
        if (saved == null) {
            throw new BizException("创建会话失败");
        }
        return toVO(saved);
    }

    /**
     * 获取当前用户会话列表（置顶优先，其次按最后消息时间倒序）
     */
    @Override
    public List<ConversationVO> list() {
        Long userId = requireUserId();
        List<Conversation> conversations = conversationMapper.selectByUserId(userId);
        return conversations.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 删除会话
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        Long userId = requireUserId();
        requireOwnerConversation(id);
        int result = conversationMapper.deleteById(id, userId);
        if (result <= 0) {
            throw new BizException("删除会话失败");
        }
    }

    /**
     * 置顶/取消置顶
     *
     * @param isPinned 目标置顶状态；为空时表示切换
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ConversationVO updatePinned(Long id, Boolean isPinned) {
        Conversation conversation = requireOwnerConversation(id);
        boolean targetPinned = isPinned != null ? isPinned : !Boolean.TRUE.equals(conversation.getIsPinned());
        int result = conversationMapper.updatePinned(id, UserContext.getUserId(), targetPinned);
        if (result <= 0) {
            throw new BizException("置顶更新失败");
        }
        Conversation updated = conversationMapper.selectById(id);
        if (updated == null) {
            throw new BizException("置顶更新失败");
        }
        return toVO(updated);
    }

    /**
     * 标记已读（不再需要，因为删除了unread_count字段）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ConversationVO markRead(Long id) {
        // 由于删除了unread_count字段，此方法仅做权限校验
        Conversation conversation = requireOwnerConversation(id);
        return toVO(conversation);
    }

    /**
     * 获取当前用户ID；若未登录则抛业务异常
     */
    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        return userId;
    }

    /**
     * 获取会话并校验归属权
     */
    private Conversation requireOwnerConversation(Long id) {
        Long userId = requireUserId();
        Conversation conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw new BizException("会话不存在");
        }
        if (!userId.equals(conversation.getUserId())) {
            throw new BizException("无权限访问该会话");
        }
        return conversation;
    }

    /**
     * 实体转VO
     */
    private ConversationVO toVO(Conversation conversation) {
        ConversationVO conversationVO = new ConversationVO();
        BeanUtil.copyProperties(conversation, conversationVO);
        
        // 查询最后一条消息
        com.chatbot.model.entity.Message lastMessage = messageMapper.selectLastByConversationId(conversation.getId());
        if (lastMessage != null) {
            conversationVO.setLastMessage(lastMessage.getContent());
            conversationVO.setLastMessageSenderType(lastMessage.getSenderType().getCode());
        } else {
            conversationVO.setLastMessage("开始聊天吧~");
            conversationVO.setLastMessageSenderType(null);
        }
        
        return conversationVO;
    }
}
