package com.companion.xcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.GameCompanionUserPhoto;
import com.business.domain.bo.GameCompanionPhotoBo;
import com.business.domain.vo.GameCompanionUserPhotoVo;
import com.business.mapper.GameCompanionUserPhotoMapper;
import com.companion.xcx.service.IXcxCompanionPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 小程序陪玩照片Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxCompanionPhotoServiceImpl implements IXcxCompanionPhotoService {

    private final GameCompanionUserPhotoMapper companionUserPhotoMapper;

    /**
     * 获取当前登录陪玩的照片列表
     *
     * @return 照片列表
     */
    @Override
    public List<GameCompanionUserPhotoVo> getPhotoList() {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<GameCompanionUserPhoto> photoWrapper = Wrappers.lambdaQuery(GameCompanionUserPhoto.class);
        photoWrapper.eq(GameCompanionUserPhoto::getCompanionId, userId);
        return companionUserPhotoMapper.selectVoList(photoWrapper);
    }

    /**
     * 修改当前登录陪玩的照片
     *
     * @param photoList 照片列表
     * @return 是否修改成功
     */
    @Override
    @Transactional
    public Boolean updatePhoto(List<GameCompanionPhotoBo> photoList) {
        Long userId = LoginHelper.getUserId();
        // 删除当前用户所有照片
        LambdaQueryWrapper<GameCompanionUserPhoto> deleteWrapper = Wrappers.lambdaQuery(GameCompanionUserPhoto.class);
        deleteWrapper.eq(GameCompanionUserPhoto::getCompanionId, userId);
        companionUserPhotoMapper.delete(deleteWrapper);

        // 批量新增照片
        if (photoList != null && !photoList.isEmpty()) {
            ArrayList<GameCompanionUserPhoto> batchInsertPhoto = new ArrayList<>();
            photoList.forEach(p -> {
                GameCompanionUserPhoto buildUserPhoto = new GameCompanionUserPhoto();
                buildUserPhoto.setCompanionId(userId);
                buildUserPhoto.setPhoto(p.getPhoto());
                batchInsertPhoto.add(buildUserPhoto);
            });
            companionUserPhotoMapper.insertBatch(batchInsertPhoto);
        }
        return true;
    }
}
