package com.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.User;
import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.business.mapper.UserMapper;
import com.business.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;

/**
 * 用户Service业务层处理
 *
 * @author Mk
 * @date 2026-06-23
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper baseMapper;

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户信息
     */
    @Override
    public UserVo selectUserByPhonenumber(String phonenumber) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getPhonenumber, phonenumber);
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 通过ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public UserVo selectUserById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 新增用户
     *
     * @param bo 用户
     * @return 是否新增成功
     */
    @Override
    public boolean insertUser(UserBo bo) {
        User add = MapstructUtils.convert(bo, User.class);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户
     *
     * @param bo 用户
     * @return 是否修改成功
     */
    @Override
    public boolean updateUser(UserBo bo) {
        User update = MapstructUtils.convert(bo, User.class);
        return baseMapper.updateById(update) > 0;
    }

}
