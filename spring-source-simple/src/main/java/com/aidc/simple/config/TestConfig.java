package com.aidc.simple.config;

import com.aidc.simple.domain.Banana;
import com.aidc.simple.domain.fruit.food.Pear;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/20
 */
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.aidc.simple.domain.fruit")
@Configuration
public class TestConfig {

    //@Bean(initMethod = "init", destroyMethod = "destroy")
    Banana banana(Pear pear) {
        return new Banana(pear);
    }

    @Bean
    RtWithContextAspect rtWithContextAspect() {
        return new RtWithContextAspect();
    }
}
