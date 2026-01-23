package com.chatbot.service.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chatbot.handler.ChatWebSocketHandler;
import com.chatbot.mapper.ConversationMapper;
import com.chatbot.mapper.MessageMapper;
import com.chatbot.model.entity.Message;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 */
@Slf4j
@Service
public class HumanLikeScheduler {

    private static final long BASE_DELAY_MS = 500;
    private static final long FACTOR_MS = 50;
    private static final long MAX_DELAY_MS = 10_000;

    @Resource
    private ChatWebSocketHandler chatWebSocketHandler;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private ConversationMapper conversationMapper;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<Long, CompletableFuture<Void>> chains = new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    public void enqueueAiReply(Long userId, Long conversationId, List<String> segments) {
        if (userId == null || conversationId == null || CollUtil.isEmpty(segments)) {
            return;
        }
        
        log.debug("加入发送队列 - 会话ID: {}, 分段数: {}", conversationId, segments.size());
        
        for (int i = 0; i < segments.size(); i++) {
            String segment = segments.get(i);
            if (segment == null) {
                continue;
            }
            int segmentIndex = i + 1;
            enqueue(conversationId, () -> sendAiSegment(userId, conversationId, segment, segmentIndex, segments.size()));
        }
    }

    private void enqueue(Long conversationId, java.util.function.Supplier<CompletableFuture<Void>> supplier) {
        chains.compute(conversationId, (k, prev) -> {
            CompletableFuture<Void> base = prev == null ? CompletableFuture.completedFuture(null) : prev;
            return base.handle((v, e) -> null).thenCompose(ignored -> supplier.get());
        });
    }

    private CompletableFuture<Void> sendAiSegment(Long userId, Long conversationId, String segment, int segmentIndex, int totalSegments) {
        long delayMs = computeDelayMs(segment);
        log.debug("分段 [{}/{}] 将在 {}ms 后发送", segmentIndex, totalSegments, delayMs);
        
        return delay(delayMs).thenRun(() -> {
            LocalDateTime now = LocalDateTime.now();
            Message aiMessage = new Message();
            aiMessage.setConversationId(conversationId);
            aiMessage.setSenderType(Message.SenderType.AI);
            aiMessage.setContent(segment);
            aiMessage.setCreatedAt(now);
            messageMapper.insert(aiMessage);

            conversationMapper.updateLastMessageTime(conversationId, userId, now);

            JSONObject payload = new JSONObject();
            payload.set("type", "TEXT");
            payload.set("conversationId", conversationId);
            payload.set("messageId", aiMessage.getId());
            payload.set("senderType", "ai");
            payload.set("content", segment);
            payload.set("createdAt", now.toString());
            chatWebSocketHandler.sendMessageToUser(userId, JSONUtil.toJsonStr(payload));
            
            log.debug("分段 [{}/{}] 已发送", segmentIndex, totalSegments);
        });
    }

    private CompletableFuture<Void> delay(long ms) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        scheduler.schedule(() -> future.complete(null), ms, TimeUnit.MILLISECONDS);
        return future;
    }

    private long computeDelayMs(String text) {
        int length = text == null ? 0 : text.length();
        long delay = BASE_DELAY_MS + (long) length * FACTOR_MS;
        return Math.min(delay, MAX_DELAY_MS);
    }
}

