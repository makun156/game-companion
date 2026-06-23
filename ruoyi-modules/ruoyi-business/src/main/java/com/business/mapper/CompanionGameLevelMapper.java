package com.business.mapper;

import com.business.domain.CompanionGameLevel;
import com.business.domain.vo.CompanionGameLevelVo;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 陪玩关联游戏段位Mapper接口
 *
 * @author Mk
 * @date 2026-06-22
 */
public interface CompanionGameLevelMapper extends BaseMapperPlus<CompanionGameLevel, CompanionGameLevelVo> {

    /**
     * 根据陪玩用户id查询关联游戏段位列表（含游戏名称、段位名称）
     *
     * @param userId 陪玩用户id
     * @return 关联游戏段位列表
     */
    List<CompanionGameLevelVo> selectVoListByUserId(@Param("userId") Long userId);

}
