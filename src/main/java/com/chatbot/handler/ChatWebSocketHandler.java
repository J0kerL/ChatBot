package com.chatbot.handler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.chatbot.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket处理器
 * 用于处理实时消息推送，包括AI回复的分段传输、输入状态推送等
 * @author Administrator
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;

    // UserId -> WebSocketSession
    private static final Map<Long, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从URL参数中获取Token
        // ws://localhost:8080/ws/chat?token=xxxxx
        String query = session.getUri().getQuery();
        String token = null;
        if (query != null && query.contains("token=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    token = param.substring(6);
                    break;
                }
            }
        }

        if (token == null) {
            log.warn("WebSocket connection attempt without token. Session ID: {}", session.getId());
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            log.warn("WebSocket connection with invalid token. Session ID: {}", session.getId());
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // 如果用户已有连接，是否踢出旧连接？暂定允许，覆盖
        SESSIONS.put(userId, session);
        session.getAttributes().put("userId", userId);
        log.info("User connected: {}, Session ID: {}", userId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received message: {}", payload);
        
        try {
            if (JSONUtil.isTypeJSON(payload)) {
                JSONObject json = JSONUtil.parseObj(payload);
                String type = json.getStr("type");
                // 简单的Ping/Pong机制
                if ("PING".equalsIgnoreCase(type)) {
                    session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
                }
            }
        } catch (Exception e) {
            log.error("Error parsing message: {}", payload, e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            // 只有当断开的session是当前存储的session时才移除，避免并发登录时的误删
            WebSocketSession currentSession = SESSIONS.get(userId);
            if (currentSession != null && currentSession.getId().equals(session.getId())) {
                SESSIONS.remove(userId);
            }
        }
        log.info("Connection closed. UserId: {}, Session ID: {}, Status: {}", userId, session.getId(), status);
    }
    
    /**
     * 发送消息给指定用户
     * @param userId 用户ID
     * @param message 消息内容 (JSON字符串)
     */
    public void sendMessageToUser(Long userId, String message) {
        WebSocketSession session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (Exception e) {
                log.error("Failed to send message to user {}", userId, e);
            }
        } else {
            log.debug("User {} not connected, message dropped or should be stored offline", userId);
        }
    }
}
