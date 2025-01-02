/*
 * Copyright 2002-2014 the original author or authors.
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
 * Callback interface triggered at the end of the singleton pre-instantiation phase
 * during {@link BeanFactory} bootstrap. This interface can be implemented by
 * singleton beans in order to perform some initialization after the regular
 * singleton instantiation algorithm, avoiding side effects with accidental early
 * initialization (e.g. from {@link ListableBeanFactory#getBeansOfType} calls).
 * In that sense, it is an alternative to {@link InitializingBean} which gets
 * triggered right at the end of a bean's local construction phase.
 *
 * <p>This callback variant is somewhat similar to
 * {@link org.springframework.context.event.ContextRefreshedEvent} but doesn't
 * require an implementation of {@link org.springframework.context.ApplicationListener},
 * with no need to filter context references across a context hierarchy etc.
 * It also implies a more minimal dependency on just the {@code beans} package
 * and is being honored by standalone {@link ListableBeanFactory} implementations,
 * not just in an {@link org.springframework.context.ApplicationContext} environment.
 *
 * <p><b>NOTE:</b> If you intend to start/manage asynchronous tasks, preferably
 * implement {@link org.springframework.context.Lifecycle} instead which offers
 * a richer model for runtime management and allows for phased startup/shutdown.
 * 接口功能
 * 回调时机：在BeanFactory引导过程中的单例预实例化阶段结束时触发。
 *
 * 替代方案：作为InitializingBean接口的替代，后者是在bean本地构造阶段结束时触发的。
 * 依赖性：相比ApplicationListener接口，该接口依赖性更低，只需要依赖beans包，
 * 并且由独立的ListableBeanFactory实现支持，而不仅仅是ApplicationContext环境。
 *
 * 详细解释
 * 回调时机
 * 单例预实例化阶段：在BeanFactory初始化过程中，会预先实例化所有的单例bean。这个回调接口会在这一阶段结束后被触发。
 * 初始化时机：实现该接口的bean可以在这一阶段之后执行初始化操作，从而确保所有的单例bean已经被实例化，
 * 但还没有被完全初始化（例如，没有调用InitializingBean的afterPropertiesSet方法）。
 *
 * 替代方案
 * InitializingBean接口：通常情况下，如果一个bean实现了InitializingBean接口，那么在bean的所有属性都被设置完毕后，
 * 会调用afterPropertiesSet方法来进行初始化。但是，这种方法可能会导致在调用getBeansOfType方法时提前初始化bean。
 * 本接口：与InitializingBean不同，这个接口是在所有的单例bean已经被实例化之后才触发，
 * 因此可以避免由于提前初始化带来的副作用。
 * 依赖性
 * ApplicationListener接口：实现ApplicationListener接口可以监听ContextRefreshedEvent事件，
 * 但这种方式需要更多的依赖，包括过滤上下文引用等。
 * 本接口：该接口依赖性较低，只需要依赖beans包，并且在独立的ListableBeanFactory实现中也能得到支持，
 * 而不仅仅局限于ApplicationContext环境中
 *
 * @author Juergen Hoeller
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 * @since 4.1
 */
public interface SmartInitializingSingleton {

    /**
     * Invoked right at the end of the singleton pre-instantiation phase,
     * with a guarantee that all regular singleton beans have been created
     * already. {@link ListableBeanFactory#getBeansOfType} calls within
     * this method won't trigger accidental side effects during bootstrap.
     * <p><b>NOTE:</b> This callback won't be triggered for singleton beans
     * lazily initialized on demand after {@link BeanFactory} bootstrap,
     * and not for any other bean scope either. Carefully use it for beans
     * with the intended bootstrap semantics only.
     * 主要功能
     * 回调时机：在单例bean预实例化阶段结束时，确保所有常规的单例bean已经被创建。
     * 安全调用getBeansOfType：在这个阶段内调用getBeansOfType方法不会触发引导过程中的副作用。
     * 注意点：这个回调仅适用于在引导过程中就已经初始化的单例bean，并不适用于引导之后懒加载（lazy-initialized）的单例bean或其他作用域的bean。
     *
     * 详细解释
     * 回调时机
     * 单例预实例化阶段结束：在BeanFactory引导过程中，所有单例bean会被预先实例化。这个回调会在所有这些单例bean创建完毕之后被触发。
     * 所有单例bean已创建：此时，所有常规的单例bean都已经创建完毕，但尚未完成整个初始化流程
     * （如调用InitializingBean的afterPropertiesSet方法）。
     * 安全调用getBeansOfType
     * 避免副作用：在这个阶段，可以安全地调用getBeansOfType方法，而不用担心引发引导过程中的副作用。例如，如果在引导过程中调用getBeansOfType，可能会导致某些bean被提前初始化，从而影响正常的引导流程。
     * 利用回调进行额外操作：利用这个回调，可以在所有单例bean创建完毕后执行一些额外的操作，而不用担心这些操作会影响引导过程。
     *
     * 注意点
     * 仅适用于引导过程中的单例bean：这个回调不会被触发给那些在引导完成后才懒加载的单例bean，也不会被触发给其他作用域（如原型作用域、会话作用域等）的bean。
     * 谨慎使用：应该谨慎使用这个回调，仅限于那些确实需要在引导过程结束时执行操作的bean。
     */
    void afterSingletonsInstantiated();

}
