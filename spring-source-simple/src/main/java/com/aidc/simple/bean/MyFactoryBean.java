package com.aidc.simple.bean;

import org.springframework.beans.factory.FactoryBean;

/**
 * Description:
 *
 * @author zhouzhongyi
 * @date 2024/9/16
 */
//@Repository
public class MyFactoryBean implements FactoryBean<MyObjectBean> {
    @Override
    public MyObjectBean getObject() throws Exception {
        return new MyObjectBean();
    }

    @Override
    public Class<?> getObjectType() {
        return MyObjectBean.class;
    }
}
