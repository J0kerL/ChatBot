package com.chatbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chatbot.common.exception.BizException;
import com.chatbot.common.result.PageResult;
import com.chatbot.mapper.AiCharacterMapper;
import com.chatbot.mapper.UserMapper;
import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.vo.AiCharacterVO;
import com.chatbot.service.AiCharacterService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Service
public class AiCharacterServiceImpl implements AiCharacterService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AiCharacterMapper aiCharacterMapper;

    /**
     * 添加AI角色
     */
    @Override
    public AiCharacterVO add(AiCharacterDTO aiCharacterDTO) {
        // 验证关联用户是否存在
        if (userMapper.selectById(aiCharacterDTO.getUserId()) == null) {
            throw new BizException("关联用户不存在");
        }

        // 设置默认头像
        aiCharacterDTO.setAvatar("https://chat-b0t.oss-cn-beijing.aliyuncs.com/avatar/defaultAiAvatar.png");
        // 设置创建时间
        aiCharacterDTO.setCreatedAt(LocalDateTime.now());
        // 设置模板标志
        aiCharacterDTO.setIsTemplate(false);

        // 插入ai角色
        int result = aiCharacterMapper.insert(aiCharacterDTO);
        if (result <= 0) {
            throw new BizException("添加AI角色失败");
        }

        // 获取插入的ai角色
        AiCharacter aiCharacter = aiCharacterMapper.selectById(aiCharacterDTO.getId());

        // 转换为VO
        AiCharacterVO aiCharacterVO = new AiCharacterVO();
        BeanUtil.copyProperties(aiCharacter, aiCharacterVO);

        // 返回ai角色VO
        return aiCharacterVO;
    }

    /**
     * 分页查询AI角色
     */
    @Override
    public PageResult page(AiCharacterPageQueryDTO aiCharacterPageQueryDTO) {
        int pageNum = aiCharacterPageQueryDTO.getPageNum();
        int pageSize = aiCharacterPageQueryDTO.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        Page<AiCharacter> page = aiCharacterMapper.query(aiCharacterPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
