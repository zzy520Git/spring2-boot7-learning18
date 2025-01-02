/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Post-processor callback interface for <i>merged</i> bean definitions at runtime.
 * {@link BeanPostProcessor} implementations may implement this sub-interface in order
 * to post-process the merged bean definition (a processed copy of the original bean
 * definition) that the Spring {@code BeanFactory} uses to create a bean instance.
 *
 * <p>The {@link #postProcessMergedBeanDefinition} method may for example introspect
 * the bean definition in order to prepare some cached metadata before post-processing
 * actual instances of a bean. It is also allowed to modify the bean definition but
 * <i>only</i> for definition properties which are actually intended for concurrent
 * modification. Essentially, this only applies to operations defined on the
 * {@link RootBeanDefinition} itself but not to the properties of its base classes.
 *
 * TODO:这段注释描述了一个 BeanPostProcessor 的子接口，该子接口允许在运行时处理合并后的 bean 定义。
 *
 * 详细解释
 * 1、接口定义：
 * 这是一个 BeanPostProcessor 的子接口，允许在运行时处理合并后的 bean 定义。
 * 2、处理合并后的 bean 定义：
 * BeanPostProcessor 的实现可以实现这个子接口来处理 Spring BeanFactory 用于创建 bean 实例的合并后的 bean 定义。
 * 合并后的 bean 定义是指经过处理的 bean 定义副本，包含了所有父类和接口的属性以及自身的属性。比如parent继承的
 * 3、处理方法：
 * postProcessMergedBeanDefinition 方法可以用来检查合并后的 bean 定义，以准备一些缓存的元数据，这些元数据将在实际处理 bean 实例前准备好。
 * 也可以用来修改 bean 定义，但只能修改那些确实预期可以并发修改的定义属性。
 * 4、修改限制：
 * 修改 bean 定义时，只能修改那些确实预期可以并发修改的定义属性。
 * 本质上，这仅适用于在 RootBeanDefinition 上定义的操作，而不适用于其基类的属性。
 *
 * @author Juergen Hoeller
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getMergedBeanDefinition
 * @since 2.5
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

    /**
     * Post-process the given merged bean definition for the specified bean.
     *
     * @param beanDefinition the merged bean definition for the bean
     * @param beanType       the actual type of the managed bean instance
     * @param beanName       the name of the bean
     * @see AbstractAutowireCapableBeanFactory#applyMergedBeanDefinitionPostProcessors
     */
    void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

    /**
     * A notification that the bean definition for the specified name has been reset,
     * and that this post-processor should clear any metadata for the affected bean.
     * <p>The default implementation is empty.
     *
     * @param beanName the name of the bean
     * @see DefaultListableBeanFactory#resetBeanDefinition
     * @since 5.1
     */
    default void resetBeanDefinition(String beanName) {
    }

}
