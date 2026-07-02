package com.companion.xcx.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 小程序模块配置类
 *
 * @author system
 */
@Configuration
@MapperScan("com.business.mapper")
public class XcxConfig {
}
