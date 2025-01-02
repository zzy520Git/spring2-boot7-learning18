package com.aidc.simple;

import com.aidc.simple.config.TestConfig;
import com.aidc.simple.domain.fruit.Apple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Description:
 * @see AnnotationAwareAspectJAutoProxyCreator
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
@Slf4j
public class App {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestConfig.class);

        context.start();

        Apple apple = context.getBean(Apple.class);

        apple.make();

        log.warn("hello world");

        Thread.sleep(1000 * 2);

        context.stop();

        log.warn("bye bye hello world");
    }
}
