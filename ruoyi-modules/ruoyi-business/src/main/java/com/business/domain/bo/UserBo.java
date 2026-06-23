package com.business.domain.bo;

import com.business.domain.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 用户业务对象 t_user
 *
 * @author Mk
 * @date 2026-06-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = User.class, reverseConvertGenerate = false)
public class UserBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 头像地址
     */
    private Long avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

}
