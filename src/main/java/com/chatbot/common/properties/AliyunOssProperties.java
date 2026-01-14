package com.chatbot.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @Author Diamond
 * @Create 2026/1/13
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class AliyunOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
