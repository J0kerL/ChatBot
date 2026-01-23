package com.chatbot.controller;

import com.chatbot.common.result.PageResult;
import com.chatbot.common.result.Result;
import com.chatbot.model.dto.SendMessageDTO;
import com.chatbot.model.vo.MessageVO;
import com.chatbot.service.MessageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public Result<MessageVO> send(@Valid @RequestBody SendMessageDTO sendMessageDTO) {
        return Result.ok(messageService.send(sendMessageDTO));
    }

    @GetMapping("/list")
    public Result<PageResult> list(@RequestParam Long conversationId,
                                   @RequestParam(defaultValue = "1") int pageNum,
                                   @RequestParam(defaultValue = "20") int pageSize) {
        return Result.ok(messageService.list(conversationId, pageNum, pageSize));
    }
}

