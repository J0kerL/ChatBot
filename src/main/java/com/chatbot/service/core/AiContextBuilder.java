package com.chatbot.service.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.entity.Message;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI上下文构建器
 * 负责构建System Prompt和对话历史
 * @author Administrator
 */
@Component
public class AiContextBuilder {

    @Value("classpath:prompts/system-prompt.st")
    private Resource systemPromptResource;

    /**
     * 构建完整的Prompt对象
     * @param character AI角色设定
     * @param historyMessages 历史消息列表（按时间正序）
     * @param newUserInput 用户的新输入
     * @return Spring AI Prompt对象
     */
    public Prompt buildPrompt(AiCharacter character, List<Message> historyMessages, String newUserInput) {
        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

        // 1. 构建并添加 System Message
        org.springframework.ai.chat.messages.Message systemMessage = buildSystemMessage(character);
        messages.add(systemMessage);

        // 2. 添加历史消息 (限制最近N条，防止Context Window溢出，这里暂定最近20条)
        if (CollUtil.isNotEmpty(historyMessages)) {
            for (Message msg : historyMessages) {
                if (Message.SenderType.USER.equals(msg.getSenderType())) {
                    messages.add(new UserMessage(msg.getContent()));
                } else {
                    messages.add(new AssistantMessage(msg.getContent()));
                }
            }
        }

        // 3. 添加用户最新输入
        if (StrUtil.isNotBlank(newUserInput)) {
            messages.add(new UserMessage(newUserInput));
        }

        return new Prompt(messages);
    }

    /**
     * 根据角色实体构建 System Message
     */
    private org.springframework.ai.chat.messages.Message buildSystemMessage(AiCharacter character) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPromptResource);
        Map<String, Object> model = new HashMap<>();
        
        model.put("name", character.getName());
        model.put("age_clause", character.getAge() != null ? "你的年龄是：" + character.getAge() + "岁" : "");
        model.put("relationship_clause", StrUtil.isNotBlank(character.getRelationship()) ? "你与用户的关系是：" + character.getRelationship() : "");
        model.put("personality_clause", CollUtil.isNotEmpty(character.getPersonalityTags()) ? "你的性格标签包括：" + StrUtil.join("、", character.getPersonalityTags()) : "");
        model.put("background_clause", StrUtil.isNotBlank(character.getBackground()) ? "你的背景故事：" + character.getBackground() : "");
        model.put("speaking_style_clause", StrUtil.isNotBlank(character.getSpeakingStyle()) ? "你的说话风格：" + character.getSpeakingStyle() : "");
        model.put("memory_clause", StrUtil.isNotBlank(character.getMemorySettings()) ? "你需要记住的信息：" + character.getMemorySettings() : "");

        return systemPromptTemplate.createMessage(model);
    }
}
