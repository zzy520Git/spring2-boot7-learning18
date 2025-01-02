package com.aidc.simple.domain;

import com.aidc.simple.domain.fruit.food.Pear;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/19
 */
@Slf4j
public class Banana {

    public Banana(Pear pear) {
        log.info("banana constructor {}", pear);
    }

    void init() {
        log.info("Banana init");
    }

    void destroy() {
        log.info("Banana destroy");
    }
}
