package com.aidc.simple.domain.fruit;

import com.aidc.simple.anno.RtWithContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
@Slf4j
@Component
@Primary
public class Apple {

    /**
     * 代理对象也会复制一份，但是获取会是null
     */
    @Autowired
    public Orange orange;

    public Orange getOrange() {
        return orange;
    }

    public Apple() {
        log.info("apple constructor");
    }

    public void make() {
        eat();
    }

    @RtWithContext
    public void eat() {
        log.info("I am eating apple " + orange.getApple());
    }

}
