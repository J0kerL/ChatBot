package com.chatbot.config;

import com.chatbot.common.properties.AliyunOssProperties;
import com.chatbot.common.util.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @Author Diamond
 * @Create 2026/1/13
 */
@Configuration
@Slf4j
public class AliyunOssConfig {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliyunOssProperties aliyunOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliyunOssProperties);
        return new AliOssUtil(aliyunOssProperties.getEndpoint(),
                aliyunOssProperties.getAccessKeyId(),
                aliyunOssProperties.getAccessKeySecret(),
                aliyunOssProperties.getBucketName());
    }
}
