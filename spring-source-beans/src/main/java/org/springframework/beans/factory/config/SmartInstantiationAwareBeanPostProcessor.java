/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

import java.lang.reflect.Constructor;

/**
 * Extension of the {@link InstantiationAwareBeanPostProcessor} interface,
 * adding a callback for predicting the eventual type of a processed bean.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. In general, application-provided
 * post-processors should simply implement the plain {@link BeanPostProcessor}
 * interface or derive from the {@link InstantiationAwareBeanPostProcessorAdapter}
 * class. New methods might be added to this interface even in point releases.
 * 详细解释
 * 1、接口定义：
 * 这是一个 InstantiationAwareBeanPostProcessor 接口的扩展。
 * 新增了一个回调方法，用于预测处理后的 bean 的最终类型。
 * 2、新增的回调方法：
 * 新增的方法允许实现者预测处理后的 bean 的最终类型。
 * 这对于某些特定的处理逻辑很有用，例如在创建代理对象时需要知道最终的类型。
 * 3、注意事项：
 * 特殊用途接口：这是一个特殊用途的接口，主要用于框架内部使用。
 * 新方法可能添加：即使在小版本更新中，也可能向此接口中添加新的方法。
 * 推荐使用：通常情况下，应用程序提供的后处理器应该实现普通的 BeanPostProcessor 接口，或者继承 InstantiationAwareBeanPostProcessorAdapter 类。
 *
 * @author Juergen Hoeller
 * @see InstantiationAwareBeanPostProcessorAdapter
 * @since 2.0.3
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

    /**
     * Predict the type of the bean to be eventually returned from this
     * processor's {@link #postProcessBeforeInstantiation} callback.
     * <p>The default implementation returns {@code null}.
     *
     * @param beanClass the raw class of the bean
     * @param beanName  the name of the bean
     * @return the type of the bean, or {@code null} if not predictable
     * @throws BeansException in case of errors
     */
    @Nullable
    default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * Determine the candidate constructors to use for the given bean.
     * <p>The default implementation returns {@code null}.
     *
     * @param beanClass the raw class of the bean (never {@code null})
     * @param beanName  the name of the bean
     * @return the candidate constructors, or {@code null} if none specified
     * @throws BeansException in case of errors
     */
    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
            throws BeansException {

        return null;
    }

    /**
     * Obtain a reference for early access to the specified bean,
     * typically for the purpose of resolving a circular reference.
     * <p>This callback gives post-processors a chance to expose a wrapper
     * early - that is, before the target bean instance is fully initialized.
     * The exposed object should be equivalent to the what
     * {@link #postProcessBeforeInitialization} / {@link #postProcessAfterInitialization}
     * would expose otherwise. Note that the object returned by this method will
     * be used as bean reference unless the post-processor returns a different
     * wrapper from said post-process callbacks. In other words: Those post-process
     * callbacks may either eventually expose the same reference or alternatively
     * return the raw bean instance from those subsequent callbacks (if the wrapper
     * for the affected bean has been built for a call to this method already,
     * it will be exposes as final bean reference by default).
     * <p>The default implementation returns the given {@code bean} as-is.
     * TODO zzy
     * 这段注释描述了 BeanPostProcessor 接口中一个名为 getEarlyBeanReference 的方法的功能，该方法允许在 bean 完全初始化之前获取对 bean 的早期引用。
     *
     * 详细解释
     * 方法定义：
     * 方法名称：getEarlyBeanReference
     * 方法作用：获取对指定 bean 的早期引用，通常用于解决循环引用的问题。
     * 早期引用：
     * 这个回调方法给后处理器一个机会，在目标 bean 实例完全初始化之前暴露一个包装器（wrapper）。
     * 暴露的对象应该相当于通过 postProcessBeforeInitialization 和 postProcessAfterInitialization 方法会暴露的对象。
     * 返回值的使用：
     * 如果后处理器通过这个方法返回的对象将作为 bean 引用使用，除非后处理器在后续的 postProcessBeforeInitialization 和 postProcessAfterInitialization 回调中返回了不同的包装器。
     * 换句话说，这些后续的回调方法可以最终暴露相同的引用，或者从这些后续的回调中返回原始的 bean 实例（如果已经为受影响的 bean 构建了包装器，
     * 并且这个方法已经被调用过，那么这个包装器将默认作为最终的 bean 引用暴露出来）。
     * 默认实现：
     * 默认实现直接返回给定的 bean 对象本身。
     *
     * @param bean     the raw bean instance
     * @param beanName the name of the bean
     * @return the object to expose as bean reference
     * (typically with the passed-in bean instance as default)
     * @throws BeansException in case of errors
     */
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
