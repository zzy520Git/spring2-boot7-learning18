package com.aidc.simple.bean.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
@Slf4j
//@Component
public class MyBeanProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        log.warn("MyBeanProcessor#postProcessBeforeInitialization beanClass={}", bean.getClass().getName());
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        log.warn("MyBeanProcessor#postProcessAfterInitialization beanName={}", beanName);
        return bean;
    }
}
