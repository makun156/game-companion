package com.business.service.impl;

import com.business.domain.vo.CompanionGameLevelVo;
import com.business.mapper.CompanionGameLevelMapper;
import com.business.service.ICompanionGameLevelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 陪玩关联游戏段位Service业务层处理
 *
 * @author Mk
 * @date 2026-06-22
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CompanionGameLevelServiceImpl implements ICompanionGameLevelService {

    private final CompanionGameLevelMapper baseMapper;

    /**
     * 根据陪玩用户id查询关联游戏段位列表（含游戏名称、段位名称）
     *
     * @param userId 陪玩用户id
     * @return 关联游戏段位列表
     */
    @Override
    public List<CompanionGameLevelVo> queryListByUserId(Long userId) {
        return baseMapper.selectVoListByUserId(userId);
    }

}
