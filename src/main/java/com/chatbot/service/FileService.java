package com.chatbot.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理服务接口
 *
 * @author Diamond
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 文件访问URL
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问URL
     */
    void deleteFile(String fileUrl);
}
