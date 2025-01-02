package com.aidc.simple.domain.fruit;

import com.aidc.simple.anno.RtWithContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
@Component
@Slf4j
public class Orange {

    @Autowired
    public Apple apple;

    public Apple getApple() {
        return apple;
    }

    public Orange() {
        log.info("orange init");
    }

    @RtWithContext
    public void eat() {
        log.info("I am eating orange " + apple);
    }
}
