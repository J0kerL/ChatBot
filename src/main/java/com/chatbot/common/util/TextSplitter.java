package com.chatbot.common.util;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本分段工具类
 * 用于将AI生成的长回复拆分为符合人类阅读习惯的短句
 * @author Administrator
 */
public class TextSplitter {

    /**
     * 简单的分句正则：匹配句号、问号、感叹号，且后面不跟引号（避免拆分引用内容）
     * 同时也考虑换行符
     */
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[^。？！?!\\n]+[。？！?!\\n]*");

    /**
     * 将长文本分割为短句列表
     * @param text 原始文本
     * @return 分段后的文本列表
     */
    public static List<String> split(String text) {
        List<String> segments = new ArrayList<>();
        if (StrUtil.isBlank(text)) {
            return segments;
        }

        // 1. 预处理：替换掉过多的连续换行
        text = text.replaceAll("\\n{3,}", "\n\n");

        // 2. 尝试按段落分割（换行符）
        String[] paragraphs = text.split("\n");

        for (String paragraph : paragraphs) {
            if (StrUtil.isBlank(paragraph)) {
                continue;
            }
            
            // 如果段落较短（例如小于30字），直接作为一个片段
            if (paragraph.length() < 30) {
                segments.add(paragraph.trim());
                continue;
            }

            // 如果段落较长，尝试按标点符号进一步分割
            Matcher matcher = SENTENCE_PATTERN.matcher(paragraph);
            while (matcher.find()) {
                String sentence = matcher.group().trim();
                if (StrUtil.isNotBlank(sentence)) {
                    segments.add(sentence);
                }
            }
        }
        
        // 3. 后处理：合并过短的片段（可选，避免“嗯。”单独成一条）
        return mergeShortSegments(segments);
    }

    private static List<String> mergeShortSegments(List<String> rawSegments) {
        List<String> result = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        for (String segment : rawSegments) {
            if (!buffer.isEmpty()) {
                // 如果缓冲区已有内容，判断加上当前片段是否过长
                if (buffer.length() + segment.length() < 10) {
                    // 合并
                    buffer.append(segment);
                } else {
                    // 提交缓冲区
                    result.add(buffer.toString());
                    buffer.setLength(0);
                    buffer.append(segment);
                }
            } else {
                buffer.append(segment);
            }
        }

        if (!buffer.isEmpty()) {
            result.add(buffer.toString());
        }

        return result;
    }
}
