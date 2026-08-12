package com.business.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 小程序登录验证信息
 *
 * @author Mk
 */
@Data
public class XcxLoginVo {

    /**
     * 授权令牌
     */
    @JsonProperty("access_token")
    private String accessToken;

    /**
     * 授权令牌 access_token 的有效期
     */
    @JsonProperty("expire_in")
    private Long expireIn;

    /**
     * 应用id
     */
    @JsonProperty("client_id")
    private String clientId;

    /**
     * 登录人类型：companion_user-陪玩 xcx_user-普通用户
     */
    private String loginUserType;

}
