package com.chatbot.controller;

import com.chatbot.common.result.Result;
import com.chatbot.model.vo.ConversationVO;
import com.chatbot.service.ConversationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会话管理控制器
 *
 * @author Administrator
 */
@Slf4j
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationService;

    /**
     * 创建会话（当前用户与指定AI角色之间的会话）
     */
    @PostMapping("/add")
    public Result<ConversationVO> create(@RequestParam Long characterId) {
        ConversationVO conversationVO = conversationService.create(characterId);
        return Result.ok(conversationVO);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/list")
    public Result<List<ConversationVO>> list() {
        return Result.ok(conversationService.list());
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        conversationService.delete(id);
        return Result.ok();
    }

    /**
     * 置顶/取消置顶
     *
     * @param pinned 可选参数；为空时表示切换置顶状态
     */
    @PutMapping("/{id}/pin")
    public Result<ConversationVO> pin(@PathVariable Long id, @RequestParam(value = "pinned", required = false) Boolean pinned) {
        return Result.ok(conversationService.updatePinned(id, pinned));
    }

    /**
     * 标记会话已读（将未读数置为0）
     */
    @PutMapping("/{id}/read")
    public Result<ConversationVO> markRead(@PathVariable Long id) {
        return Result.ok(conversationService.markRead(id));
    }
}
