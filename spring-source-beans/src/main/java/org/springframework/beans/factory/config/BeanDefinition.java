/*
 * Copyright 2002-2020 the original author or authors.
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

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} to introspect and modify property values
 * and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 * @since 19.03.2004
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

    /**
     * Scope identifier for the standard singleton scope: {@value}.
     * <p>Note that extended bean factories might support further scopes.
     *
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     */
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

    /**
     * Scope identifier for the standard prototype scope: {@value}.
     * <p>Note that extended bean factories might support further scopes.
     *
     * @see #setScope
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     */
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


    /**
     * Role hint indicating that a {@code BeanDefinition} is a major part
     * of the application. Typically corresponds to a user-defined bean.
     * 定义：指示一个 BeanDefinition 是应用程序的重要部分。通常对应于用户定义的 bean。
     * 解释：这类 bean 是由应用程序显式定义的，是应用程序的核心组成部分。例如，业务逻辑类、服务类等。
     * 它是默认值，0
     */
    int ROLE_APPLICATION = 0;

    /**
     * Role hint indicating that a {@code BeanDefinition} is a supporting
     * part of some larger configuration, typically an outer
     * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
     * {@code SUPPORT} beans are considered important enough to be aware
     * of when looking more closely at a particular
     * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
     * but not when looking at the overall configuration of an application.
     * 定义：指示一个 BeanDefinition 是某个更大配置的一部分的支持部分。通常对应于一个更大的 ComponentDefinition 的外部组件。
     * 解释：这类 bean 对特定的 ComponentDefinition 来说是重要的，但在整体的应用程序配置中不是主要关注点。它们通常支持某个特定的功能或模块，但不是应用程序的核心组成部分。
     * 示例：例如，某个模块内的辅助类或配置类。
     */
    int ROLE_SUPPORT = 1;

    /**
     * Role hint indicating that a {@code BeanDefinition} is providing an
     * entirely background role and has no relevance to the end-user. This hint is
     * used when registering beans that are completely part of the internal workings
     * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
     * 定义：指示一个 BeanDefinition 提供了一个完全的后台角色，并且对最终用户没有相关性。这种提示用于注册那些完全属于某个 ComponentDefinition 内部运作的 bean。
     * 解释：这类 bean 完全属于框架内部运作的一部分，对应用程序的最终用户来说是透明的。它们主要用于支持框架的功能，如 AOP 代理、事务管理器等。
     * 示例：例如，AOP 代理、事务管理器等内部辅助类。
     */
    int ROLE_INFRASTRUCTURE = 2;


    // Modifiable attributes

    /**
     * Set the name of the parent definition of this bean definition, if any.
     */
    void setParentName(@Nullable String parentName);

    /**
     * Return the name of the parent definition of this bean definition, if any.
     */
    @Nullable
    String getParentName();

    /**
     * Specify the bean class name of this bean definition.
     * <p>The class name can be modified during bean factory post-processing,
     * typically replacing the original class name with a parsed variant of it.
     *
     * @see #setParentName
     * @see #setFactoryBeanName
     * @see #setFactoryMethodName
     */
    void setBeanClassName(@Nullable String beanClassName);

    /**
     * Return the current bean class name of this bean definition.
     * <p>Note that this does not have to be the actual class name used at runtime, in
     * case of a child definition overriding/inheriting the class name from its parent.
     * Also, this may just be the class that a factory method is called on, or it may
     * even be empty in case of a factory bean reference that a method is called on.
     * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
     * rather only use it for parsing purposes at the individual bean definition level.
     *
     * @see #getParentName()
     * @see #getFactoryBeanName()
     * @see #getFactoryMethodName()
     */
    @Nullable
    String getBeanClassName();

    /**
     * Override the target scope of this bean, specifying a new scope name.
     *
     * @see #SCOPE_SINGLETON
     * @see #SCOPE_PROTOTYPE
     */
    void setScope(@Nullable String scope);

    /**
     * Return the name of the current target scope for this bean,
     * or {@code null} if not known yet.
     */
    @Nullable
    String getScope();

    /**
     * Set whether this bean should be lazily initialized.
     * <p>If {@code false}, the bean will get instantiated on startup by bean
     * factories that perform eager initialization of singletons.
     */
    void setLazyInit(boolean lazyInit);

    /**
     * Return whether this bean should be lazily initialized, i.e. not
     * eagerly instantiated on startup. Only applicable to a singleton bean.
     */
    boolean isLazyInit();

    /**
     * Set the names of the beans that this bean depends on being initialized.
     * The bean factory will guarantee that these beans get initialized first.
     */
    void setDependsOn(@Nullable String... dependsOn);

    /**
     * Return the bean names that this bean depends on.
     */
    @Nullable
    String[] getDependsOn();

    /**
     * Set whether this bean is a candidate for getting autowired into some other bean.
     * <p>Note that this flag is designed to only affect type-based autowiring.
     * It does not affect explicit references by name, which will get resolved even
     * if the specified bean is not marked as an autowire candidate. As a consequence,
     * autowiring by name will nevertheless inject a bean if the name matches.
     */
    void setAutowireCandidate(boolean autowireCandidate);

    /**
     * Return whether this bean is a candidate for getting autowired into some other bean.
     */
    boolean isAutowireCandidate();

    /**
     * Set whether this bean is a primary autowire candidate.
     * <p>If this value is {@code true} for exactly one bean among multiple
     * matching candidates, it will serve as a tie-breaker.
     */
    void setPrimary(boolean primary);

    /**
     * Return whether this bean is a primary autowire candidate.
     */
    boolean isPrimary();

    /**
     * Specify the factory bean to use, if any.
     * This the name of the bean to call the specified factory method on.
     *
     * @see #setFactoryMethodName
     */
    void setFactoryBeanName(@Nullable String factoryBeanName);

    /**
     * Return the factory bean name, if any.
     */
    @Nullable
    String getFactoryBeanName();

    /**
     * Specify a factory method, if any. This method will be invoked with
     * constructor arguments, or with no arguments if none are specified.
     * The method will be invoked on the specified factory bean, if any,
     * or otherwise as a static method on the local bean class.
     *
     * @see #setFactoryBeanName
     * @see #setBeanClassName
     */
    void setFactoryMethodName(@Nullable String factoryMethodName);

    /**
     * Return a factory method, if any.
     */
    @Nullable
    String getFactoryMethodName();

    /**
     * Return the constructor argument values for this bean.
     * <p>The returned instance can be modified during bean factory post-processing.
     *
     * @return the ConstructorArgumentValues object (never {@code null})
     */
    ConstructorArgumentValues getConstructorArgumentValues();

    /**
     * Return if there are constructor argument values defined for this bean.
     *
     * @since 5.0.2
     */
    default boolean hasConstructorArgumentValues() {
        return !getConstructorArgumentValues().isEmpty();
    }

    /**
     * Return the property values to be applied to a new instance of the bean.
     * <p>The returned instance can be modified during bean factory post-processing.
     *
     * @return the MutablePropertyValues object (never {@code null})
     */
    MutablePropertyValues getPropertyValues();

    /**
     * Return if there are property values defined for this bean.
     *
     * @since 5.0.2
     */
    default boolean hasPropertyValues() {
        return !getPropertyValues().isEmpty();
    }

    /**
     * Set the name of the initializer method.
     *
     * @since 5.1
     */
    void setInitMethodName(@Nullable String initMethodName);

    /**
     * Return the name of the initializer method.
     *
     * @since 5.1
     */
    @Nullable
    String getInitMethodName();

    /**
     * Set the name of the destroy method.
     *
     * @since 5.1
     */
    void setDestroyMethodName(@Nullable String destroyMethodName);

    /**
     * Return the name of the destroy method.
     *
     * @since 5.1
     */
    @Nullable
    String getDestroyMethodName();

    /**
     * Set the role hint for this {@code BeanDefinition}. The role hint
     * provides the frameworks as well as tools an indication of
     * the role and importance of a particular {@code BeanDefinition}.
     *
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     * @since 5.1
     */
    void setRole(int role);

    /**
     * Get the role hint for this {@code BeanDefinition}. The role hint
     * provides the frameworks as well as tools an indication of
     * the role and importance of a particular {@code BeanDefinition}.
     *
     * @see #ROLE_APPLICATION
     * @see #ROLE_SUPPORT
     * @see #ROLE_INFRASTRUCTURE
     */
    int getRole();

    /**
     * Set a human-readable description of this bean definition.
     *
     * @since 5.1
     */
    void setDescription(@Nullable String description);

    /**
     * Return a human-readable description of this bean definition.
     */
    @Nullable
    String getDescription();


    // Read-only attributes

    /**
     * Return a resolvable type for this bean definition,
     * based on the bean class or other specific metadata.
     * <p>This is typically fully resolved on a runtime-merged bean definition
     * but not necessarily on a configuration-time definition instance.
     *
     * @return the resolvable type (potentially {@link ResolvableType#NONE})
     * @see ConfigurableBeanFactory#getMergedBeanDefinition
     * @since 5.2
     */
    ResolvableType getResolvableType();

    /**
     * Return whether this a <b>Singleton</b>, with a single, shared instance
     * returned on all calls.
     *
     * @see #SCOPE_SINGLETON
     */
    boolean isSingleton();

    /**
     * Return whether this a <b>Prototype</b>, with an independent instance
     * returned for each call.
     *
     * @see #SCOPE_PROTOTYPE
     * @since 3.0
     */
    boolean isPrototype();

    /**
     * Return whether this bean is "abstract", that is, not meant to be instantiated.
     */
    boolean isAbstract();

    /**
     * Return a description of the resource that this bean definition
     * came from (for the purpose of showing context in case of errors).
     */
    @Nullable
    String getResourceDescription();

    /**
     * Return the originating BeanDefinition, or {@code null} if none.
     * <p>Allows for retrieving the decorated bean definition, if any.
     * <p>Note that this method returns the immediate originator. Iterate through the
     * originator chain to find the original BeanDefinition as defined by the user.
     */
    @Nullable
    BeanDefinition getOriginatingBeanDefinition();

}
