package com.companion.xcx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 小程序服务启动程序
 *
 * @author system
 */
@SpringBootApplication(scanBasePackages = {"com.companion.xcx", "com.business", "org.dromara.common", "org.dromara.system"})
@MapperScan({"com.business.mapper", "org.dromara.system.mapper"})
public class XcxApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(XcxApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  RuoYi-Xcx 小程序服务启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
