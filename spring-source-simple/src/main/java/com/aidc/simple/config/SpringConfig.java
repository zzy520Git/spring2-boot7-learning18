package com.aidc.simple.config;

import com.aidc.simple.domain.fruit.Apple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
@ComponentScan(basePackages = "com.aidc.simple.config")
@Configuration
public class SpringConfig {

    @Bean
    Apple twoApple() {

        return new Apple();
    }

    /**
     * 这是CGLIB代理对象
     */
    @Autowired
    private TestConfig testConfig;

}
