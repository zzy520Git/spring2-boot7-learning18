package com.aidc.simple.config;


import com.aidc.simple.anno.RtWithContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Description:
 * Ordered.LOWEST_PRECEDENCE代表最低优先级，around最后执行
 * Ordered.HIGHEST_PRECEDENCE代表最高优先级，around前置最先执行，around后置最后执行
 * 换句话说，LOWEST_PRECEDENCE包裹在最里层，默认是LOWEST_PRECEDENCE
 *
 * @author zhouzhongyi
 * @date 2024/6/18
 */
@Slf4j
@Aspect
//@Component
public class RtWithContextAspect {

    @Pointcut("@annotation(com.aidc.simple.anno.RtWithContext)")
    public void injectContext() {
    }

    @Around("injectContext()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        final Signature signature = pjp.getSignature();
        if (!(signature instanceof MethodSignature msig)) {
            return pjp.proceed();
        }

        Method method = msig.getMethod();
        if (method == null) {
            return pjp.proceed();
        }

        RtWithContext rtWithContext = method.getAnnotation(RtWithContext.class);

        log.warn("RtWithContextAspect before {}", method.getName());

        return pjp.proceed();

    }
}
