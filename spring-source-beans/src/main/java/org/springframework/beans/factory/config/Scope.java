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

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

/**
 * Strategy interface used by a {@link ConfigurableBeanFactory},
 * representing a target scope to hold bean instances in.
 * This allows for extending the BeanFactory's standard scopes
 * {@link ConfigurableBeanFactory#SCOPE_SINGLETON "singleton"} and
 * {@link ConfigurableBeanFactory#SCOPE_PROTOTYPE "prototype"}
 * with custom further scopes, registered for a
 * {@link ConfigurableBeanFactory#registerScope(String, Scope) specific key}.
 *
 * <p>{@link org.springframework.context.ApplicationContext} implementations
 * such as a {@link org.springframework.web.context.WebApplicationContext}
 * may register additional standard scopes specific to their environment,
 * e.g. {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST "request"}
 * and {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION "session"},
 * based on this Scope SPI.
 *
 * <p>Even if its primary use is for extended scopes in a web environment,
 * this SPI is completely generic: It provides the ability to get and put
 * objects from any underlying storage mechanism, such as an HTTP session
 * or a custom conversation mechanism. The name passed into this class's
 * {@code get} and {@code remove} methods will identify the
 * target object in the current scope.
 *
 * <p>{@code Scope} implementations are expected to be thread-safe.
 * One {@code Scope} instance can be used with multiple bean factories
 * at the same time, if desired (unless it explicitly wants to be aware of
 * the containing BeanFactory), with any number of threads accessing
 * the {@code Scope} concurrently from any number of factories.
 * TODO: zzy
 * 这段描述介绍了一个名为 Scope 的策略接口，它用于表示 Spring 框架中的目标作用域，用于保存 Bean 实例。
 * Scope 接口允许扩展 ConfigurableBeanFactory 的标准作用域，即 singleton 和 prototype 作用域，并注册自定义的作用域。
 *
 * Scope 接口的作用
 * 扩展作用域：
 * Scope 接口允许开发人员注册自定义的作用域，这些作用域可以扩展 Spring 容器的标准作用域，如单例（singleton）和原型（prototype）作用域。
 * 环境特定的作用域：
 * 在特定的应用环境中，如 Web 应用中，ApplicationContext 实现类（如 WebApplicationContext）可以注册额外的环境特定的作用域，如请求作用域（request）和会话作用域（session）。
 * 完全通用的 SPI：
 * 尽管 Scope 接口的主要用途是在 Web 环境中扩展作用域，但它是一个完全通用的 SPI（服务提供者接口），可以用于从任何底层存储机制获取和放置对象，例如 HTTP 会话或其他自定义的对话机制。
 * Scope 接口的方法
 * Scope 接口定义了一些方法，用于从作用域中获取、放置、移除对象，以及确定对象是否为单例。这些方法包括但不限于：
 *
 * Object get(String name, ObjectFactory<T> objectFactory) throws BeansException：获取指定名称的对象，如果对象不存在，则使用 objectFactory 创建它。
 * void remove(String name) throws BeansException：从作用域中移除指定名称的对象。
 * void registerDestructionCallback(String name, Runnable destructionCallback) throws BeansException：注册销毁回调，当作用域销毁时执行。
 * Object resolveContextualObject(String key) throws BeansException：解析上下文中的对象，通常用于获取请求或会话等 Web 上下文对象。
 * String getConversationId() throws IllegalStateException：获取当前会话的唯一标识符。
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see ConfigurableBeanFactory#registerScope
 * @see CustomScopeConfigurer
 * @see org.springframework.aop.scope.ScopedProxyFactoryBean
 * @see org.springframework.web.context.request.RequestScope
 * @see org.springframework.web.context.request.SessionScope
 * @since 2.0
 */
public interface Scope {

    /**
     * Return the object with the given name from the underlying scope,
     * {@link ObjectFactory#getObject() creating it}
     * if not found in the underlying storage mechanism.
     * <p>This is the central operation of a Scope, and the only operation
     * that is absolutely required.
     *
     * @param name          the name of the object to retrieve
     * @param objectFactory the {@link ObjectFactory} to use to create the scoped
     *                      object if it is not present in the underlying storage mechanism
     * @return the desired object (never {@code null})
     * @throws IllegalStateException if the underlying scope is not currently active
     */
    Object get(String name, ObjectFactory<?> objectFactory);

    /**
     * Remove the object with the given {@code name} from the underlying scope.
     * <p>Returns {@code null} if no object was found; otherwise
     * returns the removed {@code Object}.
     * <p>Note that an implementation should also remove a registered destruction
     * callback for the specified object, if any. It does, however, <i>not</i>
     * need to <i>execute</i> a registered destruction callback in this case,
     * since the object will be destroyed by the caller (if appropriate).
     * <p><b>Note: This is an optional operation.</b> Implementations may throw
     * {@link UnsupportedOperationException} if they do not support explicitly
     * removing an object.
     *
     * @param name the name of the object to remove
     * @return the removed object, or {@code null} if no object was present
     * @throws IllegalStateException if the underlying scope is not currently active
     * @see #registerDestructionCallback
     */
    @Nullable
    Object remove(String name);

    /**
     * Register a callback to be executed on destruction of the specified
     * object in the scope (or at destruction of the entire scope, if the
     * scope does not destroy individual objects but rather only terminates
     * in its entirety).
     * <p><b>Note: This is an optional operation.</b> This method will only
     * be called for scoped beans with actual destruction configuration
     * (DisposableBean, destroy-method, DestructionAwareBeanPostProcessor).
     * Implementations should do their best to execute a given callback
     * at the appropriate time. If such a callback is not supported by the
     * underlying runtime environment at all, the callback <i>must be
     * ignored and a corresponding warning should be logged</i>.
     * <p>Note that 'destruction' refers to automatic destruction of
     * the object as part of the scope's own lifecycle, not to the individual
     * scoped object having been explicitly removed by the application.
     * If a scoped object gets removed via this facade's {@link #remove(String)}
     * method, any registered destruction callback should be removed as well,
     * assuming that the removed object will be reused or manually destroyed.
     *
     * @param name     the name of the object to execute the destruction callback for
     * @param callback the destruction callback to be executed.
     *                 Note that the passed-in Runnable will never throw an exception,
     *                 so it can safely be executed without an enclosing try-catch block.
     *                 Furthermore, the Runnable will usually be serializable, provided
     *                 that its target object is serializable as well.
     * @throws IllegalStateException if the underlying scope is not currently active
     * @see org.springframework.beans.factory.DisposableBean
     * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getDestroyMethodName()
     * @see DestructionAwareBeanPostProcessor
     */
    void registerDestructionCallback(String name, Runnable callback);

    /**
     * Resolve the contextual object for the given key, if any.
     * E.g. the HttpServletRequest object for key "request".
     *
     * @param key the contextual key
     * @return the corresponding object, or {@code null} if none found
     * @throws IllegalStateException if the underlying scope is not currently active
     */
    @Nullable
    Object resolveContextualObject(String key);

    /**
     * Return the <em>conversation ID</em> for the current underlying scope, if any.
     * <p>The exact meaning of the conversation ID depends on the underlying
     * storage mechanism. In the case of session-scoped objects, the
     * conversation ID would typically be equal to (or derived from) the
     * {@link javax.servlet.http.HttpSession#getId() session ID}; in the
     * case of a custom conversation that sits within the overall session,
     * the specific ID for the current conversation would be appropriate.
     * <p><b>Note: This is an optional operation.</b> It is perfectly valid to
     * return {@code null} in an implementation of this method if the
     * underlying storage mechanism has no obvious candidate for such an ID.
     *
     * @return the conversation ID, or {@code null} if there is no
     * conversation ID for the current scope
     * @throws IllegalStateException if the underlying scope is not currently active
     */
    @Nullable
    String getConversationId();

}
