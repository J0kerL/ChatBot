package com.chatbot.controller;

import com.chatbot.common.result.PageResult;
import com.chatbot.common.result.Result;
import com.chatbot.common.util.UserContext;
import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.dto.UpdateAiCharacterDTO;
import com.chatbot.model.vo.AiCharacterVO;
import com.chatbot.service.AiCharacterService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AiCharacterController {

    @Resource
    private AiCharacterService aiCharacterService;

    /**
     * 添加AI角色
     */
    @PostMapping("/add")
    public Result<AiCharacterVO> add(@Valid @RequestBody AiCharacterDTO aiCharacterDTO) {
        AiCharacterVO aiCharacterVO = aiCharacterService.add(aiCharacterDTO);
        return Result.ok(aiCharacterVO);
    }

    /**
     * 分页查询AI角色
     */
    @GetMapping("/page")
    public Result<PageResult> page(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        AiCharacterPageQueryDTO aiCharacterPageQueryDTO = new AiCharacterPageQueryDTO();
        aiCharacterPageQueryDTO.setUserId(UserContext.getUserId());
        aiCharacterPageQueryDTO.setName(name);
        aiCharacterPageQueryDTO.setPageNum(pageNum);
        aiCharacterPageQueryDTO.setPageSize(pageSize);

        PageResult pageResult = aiCharacterService.page(aiCharacterPageQueryDTO);
        return Result.ok(pageResult);
    }

    /**
     * 查看AI角色详情
     */
    @GetMapping("/get/{id}")
    public Result<AiCharacterVO> getById(@PathVariable Long id) {
        log.info("查看AI角色详情，id: {}", id);
        AiCharacterVO aiCharacterVO = aiCharacterService.getById(id);
        return Result.ok(aiCharacterVO);
    }

    /**
     * 更新AI角色信息
     */
    @PutMapping("/update")
    public Result<AiCharacterVO> update(@Valid @RequestBody UpdateAiCharacterDTO updateAiCharacterDTO) {
        log.info("更新AI角色信息，updateAiCharacterDTO: {}", updateAiCharacterDTO);
        AiCharacterVO aiCharacterVO = aiCharacterService.update(updateAiCharacterDTO);
        return Result.ok(aiCharacterVO);
    }

    /**
     * 删除AI角色
     */
    @DeleteMapping("/del/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        aiCharacterService.delete(id);
        return Result.ok();
    }

    /**
     * 上传AI角色头像
     */
    @PostMapping("/avatar/{id}")
    public Result<String> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("上传AI角色头像请求：id={}, originalName={}, size={}",
                id, file.getOriginalFilename(), file.getSize());
        String avatarUrl = aiCharacterService.updateAvatar(id, file);
        return Result.ok(avatarUrl);
    }

}
