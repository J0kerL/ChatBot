package com.chatbot.service.impl;

import com.chatbot.common.exception.BizException;
import com.chatbot.common.util.AliOssUtil;
import com.chatbot.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 文件管理服务实现类
 *
 * @author Diamond
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private AliOssUtil aliOssUtil;

    // 最大文件大小10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    @Override
    public String uploadFile(MultipartFile file) {
        // 校验文件
        if (file == null || file.isEmpty()) {
            log.error("文件上传失败：文件为空");
            throw new BizException("请选择要上传的文件");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("文件上传失败：文件大小超过限制，size={}", file.getSize());
            throw new BizException("文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            log.error("文件上传失败：文件名为空");
            throw new BizException("文件名不能为空");
        }

        // 校验文件类型
        String extension = getFileExtension(originalFilename);
        if (!isAllowedFileType(extension)) {
            log.error("文件上传失败：不支持的文件类型，extension={}", extension);
            throw new BizException("不支持的文件类型：" + extension);
        }

        try {
            // 生成唯一的文件名：UUID + 原始扩展名
            String objectName = generateObjectName(originalFilename);
            
            // 读取文件字节
            byte[] bytes = file.getBytes();
            
            // 上传到OSS
            String fileUrl = aliOssUtil.upload(bytes, objectName);
            log.info("文件上传成功：originalName={}, objectName={}, url={}", 
                    originalFilename, objectName, fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("文件上传失败：读取文件内容异常", e);
            throw new BizException("文件读取失败");
        }
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 文件访问URL
     */
    @Override
    public String uploadAvatar(MultipartFile file) {
        // 校验文件
        if (file == null || file.isEmpty()) {
            log.error("头像上传失败：文件为空");
            throw new BizException("请选择要上传的头像");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            log.error("头像上传失败：文件大小超过限制，size={}", file.getSize());
            throw new BizException("图片大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            log.error("头像上传失败：文件名为空");
            throw new BizException("文件名不能为空");
        }

        // 校验文件类型（只允许图片）
        String extension = getFileExtension(originalFilename);
        if (!isImageFile(extension)) {
            log.error("头像上传失败：不支持的文件类型，extension={}", extension);
            throw new BizException("只支持上传图片格式（jpg、png、gif、webp）");
        }

        try {
            // 生成头像文件名：avatar/UUID.扩展名
            String objectName = generateAvatarObjectName(originalFilename);
            
            // 读取文件字节
            byte[] bytes = file.getBytes();
            
            // 上传到OSS
            String fileUrl = aliOssUtil.upload(bytes, objectName);
            log.info("头像上传成功：originalName={}, objectName={}, url={}", 
                    originalFilename, objectName, fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("头像上传失败：读取文件内容异常", e);
            throw new BizException("文件读取失败");
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问URL
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            log.error("文件删除失败：文件URL为空");
            throw new BizException("文件URL不能为空");
        }

        // 从URL中提取objectName
        String objectName = aliOssUtil.extractObjectName(fileUrl);
        if (objectName == null) {
            log.error("文件删除失败：无效的文件URL，url={}", fileUrl);
            throw new BizException("无效的文件URL");
        }

        // 删除文件
        aliOssUtil.delete(objectName);
        log.info("文件删除成功：url={}, objectName={}", fileUrl, objectName);
    }

    /**
     * 生成对象名称（带路径）
     * 格式：年月日/UUID.扩展名
     */
    private String generateObjectName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String date = java.time.LocalDate.now().toString().replace("-", "/");
        return String.format("%s/%s.%s", date, uuid, extension);
    }

    /**
     * 生成头像对象名称
     * 格式：avatar/UUID.扩展名
     */
    private String generateAvatarObjectName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return String.format("avatar/%s.%s", uuid, extension);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 判断是否为允许的文件类型
     */
    private boolean isAllowedFileType(String extension) {
        // 允许的图片类型
        String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp", 
                                 "pdf", "doc", "docx", "xls", "xlsx", "txt"};
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String extension) {
        String[] imageTypes = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String type : imageTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
