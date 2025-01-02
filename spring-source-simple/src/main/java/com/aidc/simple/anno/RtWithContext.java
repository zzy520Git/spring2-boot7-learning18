package com.aidc.simple.anno;

import java.lang.annotation.*;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/6/18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RtWithContext {
    /**
     * 是否记录耗时、时间线
     *
     * @return
     */
    boolean recordRt() default true;
}
