package com.chatbot.common.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.chatbot.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * Description:
 *
 * @Author Diamond
 * @Create 2026/1/13
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     * 文件上传
     *
     * @param bytes      文件字节数组
     * @param objectName 文件对象名称（OSS中的路径）
     * @return 文件访问URL
     * @throws BizException 上传失败时抛出异常
     */
    public String upload(byte[] bytes, String objectName) {
        // 参数校验
        if (bytes == null || bytes.length == 0) {
            log.error("上传文件失败：文件内容为空");
            throw new BizException("文件内容不能为空");
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("上传文件失败：文件名为空");
            throw new BizException("文件名不能为空");
        }

        OSS ossClient = null;
        try {
            // 创建OSSClient实例
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            // 上传文件到OSS
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
            
            // 构建文件访问路径规则 https://BucketName.Endpoint/ObjectName
            String fileUrl = String.format("https://%s.%s/%s", bucketName, endpoint, objectName);
            log.info("文件上传成功，访问路径：{}", fileUrl);
            
            return fileUrl;
            
        } catch (OSSException oe) {
            log.error("OSS服务异常：ErrorCode={}, ErrorMessage={}, RequestId={}", 
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId());
            throw new BizException("文件上传失败：" + oe.getErrorMessage());
            
        } catch (ClientException ce) {
            log.error("OSS客户端异常：{}", ce.getMessage(), ce);
            throw new BizException("文件上传失败：网络连接异常");
            
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param objectName 文件对象名称（OSS中的路径）
     * @throws BizException 删除失败时抛出异常
     */
    public void delete(String objectName) {
        // 参数校验
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("删除文件失败：文件名为空");
            throw new BizException("文件名不能为空");
        }

        OSS ossClient = null;
        try {
            // 创建OSSClient实例
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            // 删除文件
            ossClient.deleteObject(bucketName, objectName);
            log.info("文件删除成功：{}", objectName);
            
        } catch (OSSException oe) {
            log.error("OSS服务异常：ErrorCode={}, ErrorMessage={}, RequestId={}", 
                    oe.getErrorCode(), oe.getErrorMessage(), oe.getRequestId());
            throw new BizException("文件删除失败：" + oe.getErrorMessage());
            
        } catch (ClientException ce) {
            log.error("OSS客户端异常：{}", ce.getMessage(), ce);
            throw new BizException("文件删除失败：网络连接异常");
            
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从完整URL中提取objectName
     *
     * @param fileUrl 文件完整URL
     * @return objectName
     */
    public String extractObjectName(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            return null;
        }
        
        // URL格式: https://bucketName.endpoint/objectName
        String prefix = String.format("https://%s.%s/", bucketName, endpoint);
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        
        log.warn("无法从URL提取objectName：{}", fileUrl);
        return null;
    }
}
