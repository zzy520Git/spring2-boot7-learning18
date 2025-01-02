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

package org.springframework.beans.factory;

/**
 * Extension of the {@link FactoryBean} interface. Implementations may
 * indicate whether they always return independent instances, for the
 * case where their {@link #isSingleton()} implementation returning
 * {@code false} does not clearly indicate independent instances.
 *
 * <p>Plain {@link FactoryBean} implementations which do not implement
 * this extended interface are simply assumed to always return independent
 * instances if their {@link #isSingleton()} implementation returns
 * {@code false}; the exposed object is only accessed on demand.
 *
 * <p><b>NOTE:</b> This interface is a special purpose interface, mainly for
 * internal use within the framework and within collaborating frameworks.
 * In general, application-provided FactoryBeans should simply implement
 * the plain {@link FactoryBean} interface. New methods might be added
 * to this extended interface even in point releases.
 * 主要功能
 * 指示是否返回独立实例：扩展的接口允许FactoryBean的实现表明它们是否总是返回独立的对象实例。
 * 默认假设：对于没有实现该扩展接口的普通FactoryBean实现，默认情况下如果它们的isSingleton()方法返回false，
 * 则假定它们总是返回独立的实例。
 * 内部使用：该接口主要用于框架内部及合作框架之间的协作，对于一般的应用程序提供的FactoryBean实现，
 * 建议只实现基本的FactoryBean接口即可。
 *
 * 为什么需要这个扩展接口？
 * 在Spring框架中，FactoryBean是一个特殊的bean类型，它可以用来生产另一个对象（称为“产品对象”）。
 * FactoryBean提供了一个isSingleton()方法，用于指示该bean是否为单例（singleton）模式。
 * 然而，在某些情况下，即使isSingleton()方法返回false，也不能明确表示每次调用都会返回一个新的独立实例。
 * 例如，某些FactoryBean可能会缓存它们的产品对象，以便在后续调用时返回同一个实例，即使它们不是单例模式。
 *
 * 扩展接口的作用
 * 通过实现这个扩展接口，FactoryBean的实现可以更明确地告诉框架它们的行为。例如，
 * 可以新增一个方法来指示FactoryBean是否总是返回独立的实例
 *
 * @param <T> the bean type
 * @author Juergen Hoeller
 * @see #isPrototype()
 * @see #isSingleton()
 * @since 2.0.3
 */
public interface SmartFactoryBean<T> extends FactoryBean<T> {

    /**
     * Is the object managed by this factory a prototype? That is,
     * will {@link #getObject()} always return an independent instance?
     * <p>The prototype status of the FactoryBean itself will generally
     * be provided by the owning {@link BeanFactory}; usually, it has to be
     * defined as singleton there.
     * <p>This method is supposed to strictly check for independent instances;
     * it should not return {@code true} for scoped objects or other
     * kinds of non-singleton, non-independent objects. For this reason,
     * this is not simply the inverted form of {@link #isSingleton()}.
     * <p>The default implementation returns {@code false}.
     * 判断对象是否为独立实例
     * 独立实例：如果一个对象是独立的，那么每次通过getObject()方法获取该对象时都应该返回一个新的实例。
     * 这通常对应于Spring中的原型（prototype）作用域。
     * 非独立实例：如果一个对象不是独立的，那么多次调用getObject()方法可能会返回相同的实例，
     * 这通常对应于Spring中的单例（singleton）作用域。
     * FactoryBean本身的原型状态
     * 由BeanFactory管理：FactoryBean本身的原型状态通常是由拥有它的BeanFactory提供的。
     * 通常情况下，FactoryBean本身需要被定义为单例（singleton）。
     * FactoryBean实例的生命周期：FactoryBean实例的生命周期由BeanFactory管理，
     * 而FactoryBean生产的对象的生命周期则由FactoryBean本身决定。
     * 方法的具体要求
     * 严格检查独立实例：该方法应该严格检查返回的对象是否为独立的实例。它不应该返回true用于范围对象（scoped objects）或其他非单例、非独立的对象。
     * 与isSingleton方法的区别：该方法并不是简单地返回isSingleton()方法的反值。这是因为可能存在一些对象既不是单例也不是独立的原型对象，
     * 比如会话作用域（session scope）、请求作用域（request scope）等。
     *
     * @return whether the exposed object is a prototype
     * @see #getObject()
     * @see #isSingleton()
     */
    default boolean isPrototype() {
        return false;
    }

    /**
     * Does this FactoryBean expect eager initialization, that is,
     * eagerly initialize itself as well as expect eager initialization
     * of its singleton object (if any)?
     * <p>A standard FactoryBean is not expected to initialize eagerly:
     * Its {@link #getObject()} will only be called for actual access, even
     * in case of a singleton object. Returning {@code true} from this
     * method suggests that {@link #getObject()} should be called eagerly,
     * also applying post-processors eagerly. This may make sense in case
     * of a {@link #isSingleton() singleton} object, in particular if
     * post-processors expect to be applied on startup.
     * <p>The default implementation returns {@code false}.
     * 方法功能
     * 急切初始化：判断FactoryBean是否期望在容器启动时立即初始化自身及其生产的单例对象。
     * 默认实现：默认实现返回false，意味着默认情况下FactoryBean不期望急切初始化。
     * 详细解释
     * 判断是否期望急切初始化
     * 标准的FactoryBean行为：标准的FactoryBean并不期望急切初始化。这意味着它的getObject()方法只有在实际访问对象时才会被调用，
     * 即使它生产的对象是单例（singleton）的。
     * 急切初始化的含义：如果这个方法返回true，则表明FactoryBean希望它的getObject()方法在容器启动时就立即被调用，
     * 并且在这个过程中也会急切地应用后置处理器（BeanPostProcessor）。
     * 这种行为可能在FactoryBean生产的对象是单例的情况下有意义，尤其是在后置处理器期望在启动时就被应用的情况下。
     *
     * 何时返回true
     * 单例对象：如果FactoryBean生产的对象是单例，并且某些后置处理器期望在启动时就生效
     *
     * @return whether eager initialization applies
     * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
     */
    default boolean isEagerInit() {
        return false;
    }

}
