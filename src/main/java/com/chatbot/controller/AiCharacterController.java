package com.chatbot.controller;

import com.chatbot.common.result.PageResult;
import com.chatbot.common.result.Result;
import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.vo.AiCharacterVO;
import com.chatbot.service.AiCharacterService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
        aiCharacterPageQueryDTO.setName(name);
        aiCharacterPageQueryDTO.setPageNum(pageNum);
        aiCharacterPageQueryDTO.setPageSize(pageSize);

        PageResult pageResult = aiCharacterService.page(aiCharacterPageQueryDTO);
        return Result.ok(pageResult);
    }
}
