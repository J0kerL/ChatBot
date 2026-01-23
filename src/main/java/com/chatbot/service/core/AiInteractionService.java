package com.chatbot.service.core;

import com.chatbot.common.util.TextSplitter;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * AI交互核心服务
 * 封装LLM调用与响应处理
 * @author Administrator
 */
@Slf4j
@Service
public class AiInteractionService {

    private final ChatModel chatModel;
    private final AiContextBuilder contextBuilder;

    @Autowired
    public AiInteractionService(ChatModel chatModel, AiContextBuilder contextBuilder) {
        this.chatModel = chatModel;
        this.contextBuilder = contextBuilder;
    }

    /**
     * 与AI角色进行对话
     * @param character AI角色设定
     * @param historyMessages 历史消息上下文
     * @param userMessage 用户发送的新消息
     * @return 分段后的回复列表
     */
    public List<String> chat(AiCharacter character, List<Message> historyMessages, String userMessage) {
        try {
            log.info("调用AI模型 - 角色: {}, 用户消息: {}", character.getName(), userMessage);
            
            // 1. 构建 Prompt
            Prompt prompt = contextBuilder.buildPrompt(character, historyMessages, userMessage);

            // 2. 调用 LLM
            ChatResponse response = chatModel.call(prompt);
            
            String rawResponse = response.getResult().getOutput().getText();
            log.info("AI原始回复: {}", rawResponse);
            
            // 3. 过滤掉括号内的动作描述
            String filteredResponse = removeActionDescriptions(rawResponse);
            log.info("AI过滤后回复: {}", filteredResponse);

            // 4. 不再分段，直接返回完整回复
            // 这样AI的回复会更自然，不会被固定拆分成多条消息
            return Collections.singletonList(filteredResponse);

        } catch (Exception e) {
            log.error("AI交互异常 - 角色: {}, 错误: {}", character.getName(), e.getMessage(), e);
            // 降级策略：返回友好的错误提示
            return Collections.singletonList("我有点累了，休息了");
        }
    }
    
    /**
     * 移除文本中的动作描述（括号内容）
     * 支持多种括号：()、（）、[]、【】等
     */
    private String removeActionDescriptions(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // 移除各种括号及其内容
        String result = text;
        result = result.replaceAll("\\([^)]*\\)", "");  // 移除 (xxx)
        result = result.replaceAll("（[^）]*）", "");    // 移除 （xxx）
        result = result.replaceAll("\\[[^\\]]*\\]", ""); // 移除 [xxx]
        result = result.replaceAll("【[^】]*】", "");    // 移除 【xxx】
        
        // 清理多余的空格
        result = result.replaceAll("\\s+", " ").trim();
        
        return result;
    }
}
