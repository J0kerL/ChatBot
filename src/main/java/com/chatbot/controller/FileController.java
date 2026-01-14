package com.chatbot.controller;

import com.chatbot.common.result.Result;
import com.chatbot.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理控制器
 *
 * @author Diamond
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("文件上传请求：originalName={}, size={}", 
                file.getOriginalFilename(), file.getSize());
        String fileUrl = fileService.uploadFile(file);
        return Result.ok(fileUrl);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问URL
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        log.info("文件删除请求：fileUrl={}", fileUrl);
        fileService.deleteFile(fileUrl);
        return Result.ok();
    }
}
