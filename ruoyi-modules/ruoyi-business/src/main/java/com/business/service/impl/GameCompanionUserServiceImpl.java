package com.business.service.impl;

import com.business.domain.GameCompanionUserPhoto;
import com.business.domain.vo.CompanionGameLevelVo;
import com.business.domain.vo.GameCompanionUserPhotoVo;
import com.business.mapper.CompanionGameLevelMapper;
import com.business.mapper.GameCompanionUserPhotoMapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.business.domain.bo.GameCompanionUserBo;
import com.business.domain.vo.GameCompanionUserVo;
import com.business.domain.GameCompanionUser;
import com.business.mapper.GameCompanionUserMapper;
import com.business.service.IGameCompanionUserService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 陪玩表Service业务层处理
 *
 * @author Mk
 * @date 2026-06-10
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GameCompanionUserServiceImpl implements IGameCompanionUserService {

    private final GameCompanionUserMapper baseMapper;
    private final GameCompanionUserPhotoMapper companionUserPhotoMapper;
    private final CompanionGameLevelMapper companionGameLevelMapper;

    /**
     * 查询陪玩表
     *
     * @param id 主键
     * @return 陪玩表
     */
    @Override
    public GameCompanionUserVo queryById(Long id) {
        GameCompanionUserVo queryGameCompanionUserInfo = baseMapper.selectVoById(id);
        //查询照片
        LambdaQueryWrapper<GameCompanionUserPhoto> photoWrapper = Wrappers.lambdaQuery(GameCompanionUserPhoto.class);
        photoWrapper.eq(GameCompanionUserPhoto::getCompanionId, id);
        queryGameCompanionUserInfo.setPhotos(companionUserPhotoMapper.selectVoList(photoWrapper));
        //查询游戏等级
        List<CompanionGameLevelVo> companionGameLevelList = companionGameLevelMapper.selectVoListByUserId(id);
        queryGameCompanionUserInfo.setGameLevels(companionGameLevelList);
        return queryGameCompanionUserInfo;
    }

    /**
     * 分页查询陪玩表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 陪玩表分页列表
     */
    @Override
    public TableDataInfo<GameCompanionUserVo> queryPageList(GameCompanionUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GameCompanionUser> lqw = buildQueryWrapper(bo);
        Page<GameCompanionUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的陪玩表列表
     *
     * @param bo 查询条件
     * @return 陪玩表列表
     */
    @Override
    public List<GameCompanionUserVo> queryList(GameCompanionUserBo bo) {
        LambdaQueryWrapper<GameCompanionUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<GameCompanionUser> buildQueryWrapper(GameCompanionUserBo bo) {
        LambdaQueryWrapper<GameCompanionUser> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(GameCompanionUser::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), GameCompanionUser::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getGender()), GameCompanionUser::getGender, bo.getGender());
        lqw.eq(bo.getAge() != null, GameCompanionUser::getAge, bo.getAge());
        lqw.eq(StringUtils.isNotBlank(bo.getPhone()), GameCompanionUser::getPhone, bo.getPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getAvatar()), GameCompanionUser::getAvatar, bo.getAvatar());
        lqw.eq(bo.getCity() != null, GameCompanionUser::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getIntroduction()), GameCompanionUser::getIntroduction, bo.getIntroduction());
        lqw.eq(StringUtils.isNotBlank(bo.getTags()), GameCompanionUser::getTags, bo.getTags());
        lqw.eq(bo.getPricePerHour() != null, GameCompanionUser::getPricePerHour, bo.getPricePerHour());
        lqw.eq(bo.getTotalOrders() != null, GameCompanionUser::getTotalOrders, bo.getTotalOrders());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), GameCompanionUser::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getWorkStatus()), GameCompanionUser::getWorkStatus, bo.getWorkStatus());
        return lqw;
    }

    /**
     * 新增陪玩表
     *
     * @param bo 陪玩表
     * @return 是否新增成功
     */
    @Override
    @Transactional
    public Boolean insertByBo(GameCompanionUserBo bo) {
        GameCompanionUser add = MapstructUtils.convert(bo, GameCompanionUser.class);
        validEntityBeforeSave(add);
        baseMapper.insert(add);
        ArrayList<GameCompanionUserPhoto> batchInsertPhoto = new ArrayList<>();
        bo.getPhotos().forEach(p -> {
            GameCompanionUserPhoto buildUserPhoto = new GameCompanionUserPhoto();
            buildUserPhoto.setCompanionId(add.getId());
            buildUserPhoto.setPhoto(p.getPhoto());
            batchInsertPhoto.add(buildUserPhoto);
        });
        companionUserPhotoMapper.insertBatch(batchInsertPhoto);
        return true;
    }

    /**
     * 修改陪玩表
     *
     * @param bo 陪玩表
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(GameCompanionUserBo bo) {
        GameCompanionUser update = MapstructUtils.convert(bo, GameCompanionUser.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    private static final java.util.regex.Pattern PHONE_PATTERN =
        java.util.regex.Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(GameCompanionUser entity) {
        String phone = entity.getPhone();
        // 1. 校验手机号格式
        if (StringUtils.isBlank(phone) || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ServiceException("手机号格式不合法");
        }
        // 2. 校验手机号唯一性
        LambdaQueryWrapper<GameCompanionUser> lqw = Wrappers.lambdaQuery();
        lqw.eq(GameCompanionUser::getPhone, phone);
        lqw.ne(entity.getId() != null, GameCompanionUser::getId, entity.getId());
        if (baseMapper.selectCount(lqw) > 0) {
            throw new ServiceException("该手机号已被注册");
        }
    }

    /**
     * 校验并批量删除陪玩表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
