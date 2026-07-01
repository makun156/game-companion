package org.dromara.common.web.config;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.dromara.common.core.utils.ObjectUtils;
import org.dromara.common.web.handler.GlobalExceptionHandler;
import org.dromara.common.web.interceptor.PlusWebInvokeTimeInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

/**
 * 通用配置
 *
 * @author Lion Li
 */
@AutoConfiguration
public class ResourcesConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 全局访问性能拦截
        registry.addInterceptor(new PlusWebInvokeTimeInterceptor());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 全局日期格式转换配置
        registry.addConverter(String.class, Date.class, source -> {
            DateTime parse = DateUtil.parse(source);
            if (ObjectUtils.isNull(parse)) {
                return null;
            }
            return parse.toJdkDate();
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    /**
     * 跨域配置：临时解决本地开发跨域问题
     * 生产环境建议由 nginx 统一处理，可关闭此处配置
     * @see nginx.conf
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // 允许所有来源（生产环境建议指定具体域名）
            .allowedOriginPatterns("*")
            // 允许的请求方式
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            // 允许的请求头
            .allowedHeaders("*")
            // 是否允许发送Cookie
            .allowCredentials(true)
            // 预检请求的有效期（秒），有效期内无需再次发送预检请求
            .maxAge(3600)
            // 暴露的响应头
            .exposedHeaders("Content-Disposition", "Content-Length");
    }

    /**
     * CorsFilter 配置，处理 OPTIONS 预检请求
     * 优先级高于拦截器，确保跨域请求在被拦截前处理
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带凭证
        config.setAllowCredentials(true);
        // 预检请求缓存时间
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有URL生效
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 全局异常处理器
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
