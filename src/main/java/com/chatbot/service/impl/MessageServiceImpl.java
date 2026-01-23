package com.chatbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.chatbot.common.exception.BizException;
import com.chatbot.common.result.PageResult;
import com.chatbot.common.util.UserContext;
import com.chatbot.mapper.AiCharacterMapper;
import com.chatbot.mapper.ConversationMapper;
import com.chatbot.mapper.MessageMapper;
import com.chatbot.model.dto.SendMessageDTO;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.entity.Conversation;
import com.chatbot.model.entity.Message;
import com.chatbot.model.vo.MessageVO;
import com.chatbot.service.MessageService;
import com.chatbot.service.core.AiInteractionService;
import com.chatbot.service.core.HumanLikeScheduler;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private ConversationMapper conversationMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private AiCharacterMapper aiCharacterMapper;
    @Resource
    private AiInteractionService aiInteractionService;
    @Resource
    private HumanLikeScheduler humanLikeScheduler;

    private final ExecutorService aiExecutor = Executors.newFixedThreadPool(4);

    @PreDestroy
    public void shutdown() {
        aiExecutor.shutdown();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public MessageVO send(SendMessageDTO sendMessageDTO) {
        Long userId = requireUserId();
        if (sendMessageDTO == null) {
            throw new BizException("请求参数不能为空");
        }
        Long conversationId = sendMessageDTO.getConversationId();
        if (conversationId == null) {
            throw new BizException("会话ID不能为空");
        }

        Conversation conversation = requireOwnerConversation(conversationId, userId);

        LocalDateTime now = LocalDateTime.now();
        Message userMessage = new Message();
        userMessage.setConversationId(conversationId);
        userMessage.setSenderType(Message.SenderType.USER);
        userMessage.setContent(sendMessageDTO.getContent());
        userMessage.setCreatedAt(now);

        int inserted = messageMapper.insert(userMessage);
        if (inserted <= 0 || userMessage.getId() == null) {
            throw new BizException("发送消息失败");
        }

        int touched = conversationMapper.updateLastMessageTime(conversationId, userId, now);
        if (touched <= 0) {
            throw new BizException("发送消息失败");
        }

        String userContent = userMessage.getContent();
        Long characterId = conversation.getCharacterId();
        aiExecutor.submit(() -> generateAndScheduleAiReply(userId, conversationId, characterId, userContent));

        return toVO(userMessage);
    }

    @Override
    public PageResult list(Long conversationId, int pageNum, int pageSize) {
        Long userId = requireUserId();
        if (conversationId == null) {
            throw new BizException("会话ID不能为空");
        }
        requireOwnerConversation(conversationId, userId);

        PageHelper.startPage(pageNum, pageSize);
        Page<Message> page = messageMapper.pageByConversationId(conversationId);
        List<MessageVO> records = new ArrayList<>();
        if (CollUtil.isNotEmpty(page.getResult())) {
            for (Message message : page.getResult()) {
                records.add(toVO(message));
            }
        }
        return new PageResult(page.getTotal(), records);
    }

    private void generateAndScheduleAiReply(Long userId, Long conversationId, Long characterId, String userContent) {
        try {
            org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
            
            AiCharacter aiCharacter = aiCharacterMapper.selectById(characterId);
            if (aiCharacter == null) {
                log.warn("AI角色不存在，角色ID: {}", characterId);
                return;
            }

            List<Message> history = messageMapper.selectRecentByConversationId(conversationId, 20);
            List<Message> contextHistory = trimLatestUserEcho(history, userContent);

            List<String> segments = aiInteractionService.chat(aiCharacter, contextHistory, userContent);
            humanLikeScheduler.enqueueAiReply(userId, conversationId, segments);
            
            log.info("AI回复已加入发送队列 - 会话ID: {}, 分段数: {}", conversationId, segments.size());
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(this.getClass()).error("AI回复生成任务执行失败", e);
        }
    }

    private List<Message> trimLatestUserEcho(List<Message> history, String userContent) {
        if (CollUtil.isEmpty(history) || StrUtil.isBlank(userContent)) {
            return history;
        }
        Message last = history.get(history.size() - 1);
        if (last != null
                && Message.SenderType.USER.equals(last.getSenderType())
                && userContent.equals(last.getContent())) {
            return history.subList(0, history.size() - 1);
        }
        return history;
    }

    private MessageVO toVO(Message message) {
        MessageVO messageVO = new MessageVO();
        BeanUtil.copyProperties(message, messageVO);
        if (message.getSenderType() != null) {
            messageVO.setSenderType(message.getSenderType().getCode());
        }
        return messageVO;
    }

    private Long requireUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BizException("未登录");
        }
        return userId;
    }

    private Conversation requireOwnerConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BizException("会话不存在");
        }
        if (!userId.equals(conversation.getUserId())) {
            throw new BizException("无权限访问该会话");
        }
        return conversation;
    }
}

