/*
 * Copyright 2002-2021 the original author or authors.
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
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-instantiation callback,
 * and a callback after instantiation but before explicit properties are set or
 * autowiring occurs.
 *
 * <p>Typically used to suppress default instantiation for specific target beans,
 * for example to create proxies with special TargetSources (pooling targets,
 * lazily initializing targets, etc), or to implement additional injection strategies
 * such as field injection.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework. It is recommended to implement the plain
 * {@link BeanPostProcessor} interface as far as possible.
 * 详细解释 TODO
 * 1、接口定义：
 * 这是一个 BeanPostProcessor 的子接口。
 * 它增加了两个回调方法：
 * 实例化之前的回调（before-instantiation callback）：在 bean 实例化之前调用。
 * 实例化之后但在设置显式属性或自动装配之前的回调（callback after instantiation but before explicit properties are set or autowiring occurs）：
 * 在 bean 实例化之后但在设置显式属性或自动装配之前调用。
 *
 * 2、典型用途：
 * 抑制默认实例化：用于抑制特定目标 bean 的默认实例化，例如创建带有特殊 TargetSource 的代理（如池化的目标、懒加载的目标等）。
 * 实现额外的注入策略：例如实现字段注入（field injection）等额外的注入策略。
 *
 * 3、注意事项：
 * 特殊用途：这是一个特殊用途的接口，主要用于框架内部。
 * 推荐使用：建议尽可能使用普通的 BeanPostProcessor 接口。
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#setCustomTargetSourceCreators
 * @see org.springframework.aop.framework.autoproxy.target.LazyInitTargetSourceCreator
 * @since 1.2
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    /**
     * Apply this BeanPostProcessor <i>before the target bean gets instantiated</i>.
     * The returned bean object may be a proxy to use instead of the target bean,
     * effectively suppressing default instantiation of the target bean.
     * <p>If a non-null object is returned by this method, the bean creation process
     * will be short-circuited. The only further processing applied is the
     * {@link #postProcessAfterInitialization} callback from the configured
     * {@link BeanPostProcessor BeanPostProcessors}.
     * <p>This callback will be applied to bean definitions with their bean class,
     * as well as to factory-method definitions in which case the returned bean type
     * will be passed in here.
     * <p>Post-processors may implement the extended
     * {@link SmartInstantiationAwareBeanPostProcessor} interface in order
     * to predict the type of the bean object that they are going to return here.
     * <p>The default implementation returns {@code null}.
     *
     * @param beanClass the class of the bean to be instantiated
     * @param beanName  the name of the bean
     * @return the bean object to expose instead of a default instance of the target bean,
     * or {@code null} to proceed with default instantiation
     *
     * 注释解析
     * 这段注释描述了 BeanPostProcessor 接口中 postProcessBeforeInstantiation 方法的功能，该方法在目标 bean 实例化之前应用。
     *
     * 详细解释
     * 1、方法定义：
     * 方法名称：postProcessBeforeInstantiation
     * 方法作用：在目标 bean 实例化之前应用，可以返回一个替代对象来代替目标 bean 的默认实例化。
     * 2、返回值意义：
     * 如果返回一个非空对象，则 bean 创建过程将被短路（short-circuited），即不再进行默认的实例化。
     * 返回的对象可能是用来代替目标 bean 的代理对象。
     * 如果返回 null，则继续进行默认的实例化过程。
     * 3、进一步处理：
     * 如果返回非空对象，唯一进一步的处理将是调用配置的 BeanPostProcessor 中的 postProcessAfterInitialization 回调方法。
     * 这意味着返回的对象将跳过正常的属性填充和其他初始化处理，直接进入 postProcessAfterInitialization 阶段。
     * 4、适用场景：
     * 该回调将应用于具有 bean 类的 bean 定义，也适用于工厂方法定义。在这种情况下，返回的 bean 类型将传递给此方法。
     * 例如，可以用来创建带有特殊 TargetSource 的代理（如池化的目标、懒加载的目标等）。
     * 5、扩展接口：
     * 可以实现扩展的 SmartInstantiationAwareBeanPostProcessor 接口，以便预测将在此处返回的 bean 对象类型。
     * 默认实现：
     * 默认实现返回 null，表示继续默认的实例化过程。
     *
     * @throws BeansException in case of errors
     * @see #postProcessAfterInstantiation
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getBeanClass()
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getFactoryMethodName()
     */
    @Nullable
    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    /**
     * Perform operations after the bean has been instantiated, via a constructor or factory method,
     * but before Spring property population (from explicit properties or autowiring) occurs.
     * <p>This is the ideal callback for performing custom field injection on the given bean
     * instance, right before Spring's autowiring kicks in.
     * <p>The default implementation returns {@code true}.
     *
     * @param bean     the bean instance created, with properties not having been set yet
     * @param beanName the name of the bean
     * @return {@code true} if properties should be set on the bean; {@code false}
     * if property population should be skipped. Normal implementations should return {@code true}.
     * Returning {@code false} will also prevent any subsequent InstantiationAwareBeanPostProcessor
     * instances being invoked on this bean instance.
     * 详细解析
     * 1、执行时机：
     * 该方法在 bean 已经被实例化（通过构造函数或工厂方法）之后，但在 Spring 属性填充之前调用。
     * 这是在 Spring 的自动装配机制开始之前执行的最后一个机会，因此非常适合执行自定义的字段注入或其他初始化操作。
     * 2、典型用途：
     * 自定义字段注入：在 Spring 的自动装配开始之前，可以在这个回调中执行自定义的字段注入。
     * 其他初始化操作：可以在这个回调中执行一些其他的初始化操作，如设置某些默认值等。
     * 3、返回值的意义：
     * 返回 true：表示允许继续执行 Spring 的属性填充机制，包括从显式属性设置和自动装配。
     * 返回 false：表示跳过属性填充，同时也会阻止后续的 InstantiationAwareBeanPostProcessor 实例对该 bean 的处理。
     * @throws BeansException in case of errors
     * @see #postProcessBeforeInstantiation
     */
    default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    /**
     * Post-process the given property values before the factory applies them
     * to the given bean, without any need for property descriptors.
     * <p>Implementations should return {@code null} (the default) if they provide a custom
     * {@link #postProcessPropertyValues} implementation, and {@code pvs} otherwise.
     * In a future version of this interface (with {@link #postProcessPropertyValues} removed),
     * the default implementation will return the given {@code pvs} as-is directly.
     *
     * @param pvs      the property values that the factory is about to apply (never {@code null})
     * @param bean     the bean instance created, but whose properties have not yet been set
     * @param beanName the name of the bean
     * @return the actual property values to apply to the given bean (can be the passed-in
     * PropertyValues instance), or {@code null} which proceeds with the existing properties
     * but specifically continues with a call to {@link #postProcessPropertyValues}
     * (requiring initialized {@code PropertyDescriptor}s for the current bean class)
     * @throws BeansException in case of errors
     * @see #postProcessPropertyValues
     * @since 5.1
     */
    @Nullable
    default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {

        return null;
    }

    /**
     * Post-process the given property values before the factory applies them
     * to the given bean. Allows for checking whether all dependencies have been
     * satisfied, for example based on a "Required" annotation on bean property setters.
     * <p>Also allows for replacing the property values to apply, typically through
     * creating a new MutablePropertyValues instance based on the original PropertyValues,
     * adding or removing specific values.
     * <p>The default implementation returns the given {@code pvs} as-is.
     *
     * @param pvs      the property values that the factory is about to apply (never {@code null})
     * @param pds      the relevant property descriptors for the target bean (with ignored
     *                 dependency types - which the factory handles specifically - already filtered out)
     * @param bean     the bean instance created, but whose properties have not yet been set
     * @param beanName the name of the bean
     * @return the actual property values to apply to the given bean (can be the passed-in
     * PropertyValues instance), or {@code null} to skip property population
     * @throws BeansException in case of errors
     * @see #postProcessProperties
     * @see org.springframework.beans.MutablePropertyValues
     * @deprecated as of 5.1, in favor of {@link #postProcessProperties(PropertyValues, Object, String)}
     */
    @Deprecated
    @Nullable
    default PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

        return pvs;
    }

}
