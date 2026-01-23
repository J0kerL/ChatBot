package com.chatbot.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.chatbot.common.exception.BizException;
import com.chatbot.common.result.PageResult;
import com.chatbot.common.util.AliOssUtil;
import com.chatbot.common.util.UserContext;
import com.chatbot.mapper.*;
import com.chatbot.model.dto.AiCharacterDTO;
import com.chatbot.model.dto.AiCharacterPageQueryDTO;
import com.chatbot.model.dto.UpdateAiCharacterDTO;
import com.chatbot.model.entity.AiCharacter;
import com.chatbot.model.vo.AiCharacterVO;
import com.chatbot.service.AiCharacterService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @Author Diamond
 * @Create 2026/1/15
 */
@Service
@Slf4j
public class AiCharacterServiceImpl implements AiCharacterService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private AiCharacterMapper aiCharacterMapper;
    @Resource
    private ConversationMapper conversationMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private LongTermMemoryMapper longTermMemoryMapper;
    @Resource
    private AliOssUtil aliOssUtil;


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

        log.info("AI角色添加成功 - ID: {}, 名称: {}", aiCharacter.getId(), aiCharacter.getName());
        
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

    /**
     * 查看AI角色详情
     *
     * @param id AI角色ID
     * @return AI角色VO
     */
    @Override
    public AiCharacterVO getById(Long id) {
        // 1. 参数验证
        if (id == null || id <= 0) {
            throw new BizException("角色ID不能为空");
        }

        // 2. 查询AI角色
        AiCharacter aiCharacter = aiCharacterMapper.selectById(id);
        if (aiCharacter == null) {
            throw new BizException("AI角色不存在");
        }

        // 3. 转换为VO
        AiCharacterVO aiCharacterVO = new AiCharacterVO();
        BeanUtil.copyProperties(aiCharacter, aiCharacterVO);

        return aiCharacterVO;
    }

    /**
     * 更新AI角色信息
     * 更新逻辑：
     * 1. 验证AI角色是否存在
     * 2. 验证当前用户是否有权限修改
     * 3. 动态更新字段
     * 4. 返回更新后的数据
     *
     * @param updateAiCharacterDTO 更新DTO
     * @return 更新后的AI角色VO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AiCharacterVO update(UpdateAiCharacterDTO updateAiCharacterDTO) {
        // 1. 验证AI角色是否存在
        Long id = updateAiCharacterDTO.getId();
        AiCharacter aiCharacter = aiCharacterMapper.selectById(id);
        if (aiCharacter == null) {
            throw new BizException("AI角色不存在");
        }

        // 2. 验证权限：只有创建者可以修改自己的AI角色
        if (!aiCharacter.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权限修改该AI角色");
        }

        // 3. 动态更新
        int result = aiCharacterMapper.updateSelective(updateAiCharacterDTO);
        if (result <= 0) {
            throw new BizException("更新AI角色失败");
        }

        // 4. 查询更新后的数据
        AiCharacter updatedAiCharacter = aiCharacterMapper.selectById(id);

        // 5. 转换为VO
        AiCharacterVO aiCharacterVO = new AiCharacterVO();
        BeanUtil.copyProperties(updatedAiCharacter, aiCharacterVO);

        return aiCharacterVO;
    }

    /**
     * 删除AI角色
     * 删除逻辑：
     * 1. 验证AI角色是否存在
     * 2. 验证当前用户是否有权限删除
     * 3. 查询该角色关联的所有对话ID
     * 4. 批量删除所有对话的消息记录
     * 5. 删除所有对话记录
     * 6. 删除长期记忆记录
     * 7. 删除AI角色
     *
     * @param id AI角色ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        // 1. 参数验证
        if (id == null || id <= 0) {
            throw new BizException("角色ID不能为空");
        }

        // 2. 验证AI角色是否存在
        AiCharacter aiCharacter = aiCharacterMapper.selectById(id);
        if (aiCharacter == null) {
            throw new BizException("AI角色不存在");
        }

        // 3. 验证权限：只有创建者可以删除自己的AI角色
        if (!aiCharacter.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权限删除该AI角色");
        }

        // 4. 查询该角色关联的所有对话ID
        List<Long> conversationIds = conversationMapper.selectIdsByCharacterId(id);

        // 5. 如果存在对话，批量删除所有对话的消息记录
        if (conversationIds != null && !conversationIds.isEmpty()) {
            for (Long conversationId : conversationIds) {
                messageMapper.deleteByConversationId(conversationId);
            }
        }

        // 6. 删除该角色的所有对话记录
        conversationMapper.deleteByCharacterId(id);

        // 7. 删除该角色的长期记忆记录
        longTermMemoryMapper.deleteByCharacterId(id);

        // 8. 删除AI角色
        int result = aiCharacterMapper.deleteById(id);
        if (result <= 0) {
            throw new BizException("删除AI角色失败");
        }
    }

    /**
     * 更新AI角色头像
     *
     * @param id   AI角色ID
     * @param file 头像文件
     * @return 头像地址
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String updateAvatar(Long id, MultipartFile file) {
        // 1. 验证AI角色是否存在
        AiCharacter aiCharacter = aiCharacterMapper.selectById(id);
        if (aiCharacter == null) {
            throw new BizException("AI角色不存在");
        }

        // 2. 验证权限：只有创建者可以修改自己的AI角色
        if (!aiCharacter.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权限修改该AI角色");
        }

        // 3. 验证文件
        if (file == null || file.isEmpty()) {
            throw new BizException("文件不能为空");
        }

        // 4. 生成文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BizException("文件名不能为空");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = "avatar/" + UUID.randomUUID() + extension;

        // 5. 上传到OSS
        String avatarUrl;
        try {
            avatarUrl = aliOssUtil.upload(file.getBytes(), fileName);
        } catch (IOException e) {
            throw new BizException("头像上传失败: " + e.getMessage());
        }

        // 6. 更新数据库
        UpdateAiCharacterDTO updateAiCharacterDTO = new UpdateAiCharacterDTO();
        updateAiCharacterDTO.setId(id);
        updateAiCharacterDTO.setAvatar(avatarUrl);
        int result = aiCharacterMapper.updateSelective(updateAiCharacterDTO);
        if (result <= 0) {
            throw new BizException("更新头像失败");
        }

        return avatarUrl;
    }

}
