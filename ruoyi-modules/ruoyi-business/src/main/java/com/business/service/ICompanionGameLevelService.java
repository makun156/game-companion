package com.business.service;

import com.business.domain.vo.CompanionGameLevelVo;

import java.util.List;

/**
 * 陪玩关联游戏段位Service接口
 *
 * @author Mk
 * @date 2026-06-22
 */
public interface ICompanionGameLevelService {

    /**
     * 根据陪玩用户id查询关联游戏段位列表（含游戏名称、段位名称）
     *
     * @param userId 陪玩用户id
     * @return 关联游戏段位列表
     */
    List<CompanionGameLevelVo> queryListByUserId(Long userId);

}
