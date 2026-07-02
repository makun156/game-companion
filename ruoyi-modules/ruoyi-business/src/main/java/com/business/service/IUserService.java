package com.business.service;

import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.business.domain.vo.XcxLoginVo;

/**
 * 用户Service接口
 *
 * @author Mk
 * @date 2026-06-23
 */
public interface IUserService {

    /**
     * 微信小程序一键登录
     *
     * @param xcxCode 小程序code
     * @return 登录信息
     */
    XcxLoginVo login(String xcxCode);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVo getInfo();

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户信息
     */
    UserVo selectUserByPhonenumber(String phonenumber);

    /**
     * 通过ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    UserVo selectUserById(Long id);

    /**
     * 新增用户
     *
     * @param bo 用户
     * @return 是否新增成功
     */
    boolean insertUser(UserBo bo);

    /**
     * 修改用户
     *
     * @param bo 用户
     * @return 是否修改成功
     */
    boolean updateUser(UserBo bo);

}
