package com.companion.xcx.service;

import com.business.domain.bo.GameCompanionPhotoBo;
import com.business.domain.vo.GameCompanionUserPhotoVo;

import java.util.List;

/**
 * 小程序陪玩照片Service接口
 *
 * @author system
 */
public interface IXcxCompanionPhotoService {

    /**
     * 获取当前登录陪玩的照片列表
     *
     * @return 照片列表
     */
    List<GameCompanionUserPhotoVo> getPhotoList();

    /**
     * 修改当前登录陪玩的照片
     *
     * @param photoList 照片列表
     * @return 是否修改成功
     */
    Boolean updatePhoto(List<GameCompanionPhotoBo> photoList);
}
