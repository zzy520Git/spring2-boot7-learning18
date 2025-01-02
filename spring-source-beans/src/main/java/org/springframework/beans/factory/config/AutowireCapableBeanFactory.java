/*
 * Copyright 2002-2019 the original author or authors.
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
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * Extension of the {@link BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * 主要功能：TODO
 * 自动装配能力：AutowireCapableBeanFactory提供了自动装配的能力，允许对现有bean实例进行自动装配，即使这些bean不是由Spring直接管理其生命周期的。
 * 设计目的
 * 其他框架的集成：其他框架的集成代码可以利用这个接口来装配和填充那些Spring不直接管理其生命周期的bean实例。例如，WebWork Actions 和 Tapestry Page对象就可以从中受益。
 * 使用场景
 * 非正常应用代码：这个接口并不是为了在常规的应用程序代码中直接使用。对于典型的使用场景，建议使用BeanFactory或ListableBeanFactory接口。
 * 接口意图
 * 框架内部使用：虽然ApplicationContext提供了高级的上下文环境，但是ApplicationContext本身并没有实现AutowireCapableBeanFactory接口，因为它很少被应用程序代码直接使用。但是，可以通过ApplicationContext的getAutowireCapableBeanFactory()方法来获取AutowireCapableBeanFactory的实例。
 * 方法概览
 * 虽然注释没有详细列出具体的方法，但是AutowireCapableBeanFactory通常会包括与自动装配相关的方法，比如：
 *
 * autowireBean(Properties properties)
 * autowireBean(Object existingBean)
 * initializeBean(Object existingBean, String beanName)
 * 这些方法允许你对现有的bean实例进行自动装配和初始化。
 *
 * @author Juergen Hoeller
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 * @since 04.12.2003
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

    /**
     * Constant that indicates no externally defined autowiring. Note that
     * BeanFactoryAware etc and annotation-driven injection will still be applied.
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_NO = 0;

    /**
     * Constant that indicates autowiring bean properties by name
     * (applying to all bean property setters).
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_NAME = 1;

    /**
     * Constant that indicates autowiring bean properties by type
     * (applying to all bean property setters).
     *
     * @see #createBean
     * @see #autowire
     * @see #autowireBeanProperties
     */
    int AUTOWIRE_BY_TYPE = 2;

    /**
     * Constant that indicates autowiring the greediest constructor that
     * can be satisfied (involves resolving the appropriate constructor).
     *
     * @see #createBean
     * @see #autowire
     */
    int AUTOWIRE_CONSTRUCTOR = 3;

    /**
     * Constant that indicates determining an appropriate autowire strategy
     * through introspection of the bean class.
     *
     * @see #createBean
     * @see #autowire
     * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
     * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
     */
    @Deprecated
    int AUTOWIRE_AUTODETECT = 4;

    /**
     * Suffix for the "original instance" convention when initializing an existing
     * bean instance: to be appended to the fully-qualified bean class name,
     * e.g. "com.mypackage.MyClass.ORIGINAL", in order to enforce the given instance
     * to be returned, i.e. no proxies etc.
     *
     * @see #initializeBean(Object, String)
     * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
     * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
     * @since 5.1
     */
    String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";


    //-------------------------------------------------------------------------
    // TODO：Typical methods for creating and populating external bean instances
    //-------------------------------------------------------------------------

    /**
     * Fully create a new bean instance of the given class.
     * <p>Performs full initialization of the bean, including all applicable
     * {@link BeanPostProcessor BeanPostProcessors}.
     * <p>Note: This is intended for creating a fresh instance, populating annotated
     * fields and methods as well as applying all standard bean initialization callbacks.
     * It does <i>not</i> imply traditional by-name or by-type autowiring of properties;
     * use {@link #createBean(Class, int, boolean)} for those purposes.
     * 这段注释描述了一个方法，该方法用于完全创建一个新的bean实例，并执行bean的所有初始化步骤，包括应用所有适用的BeanPostProcessor。
     *
     * 以下是这段注释的详细解释：
     *
     * 主要功能
     * 完全创建一个新的bean实例：创建一个新的bean实例，并执行所有必要的初始化步骤。
     * 初始化步骤
     * 完全初始化：这个方法会执行bean的完全初始化，包括所有适用的BeanPostProcessor。BeanPostProcessor是Spring框架提供的一个接口，允许在bean初始化前后添加自定义处理逻辑。
     * 初始化细节
     * 创建新鲜实例：创建一个新的bean实例。
     * 填充注解字段和方法：填充带有注解的字段和方法。
     * 应用标准初始化回调：应用所有标准的bean初始化回调，如@PostConstruct注解的方法等。
     * 注意事项
     * 不涉及传统的自动装配：这个方法不涉及传统的按名称或按类型的自动装配（autowiring）。如果需要进行传统的按名称或按类型的自动装配，请使用createBean(Class, int, boolean)方法。
     * 使用场景
     * 这个方法适用于需要完全控制bean的创建和初始化流程的场景。它不仅创建一个新的bean实例，还会确保所有相关的初始化步骤都得到执行，包括但不限于：
     *
     * 创建bean实例：使用无参构造函数创建一个新的bean实例。
     * 填充注解字段和方法：自动注入带有@Autowired等注解的字段或方法。
     * 应用初始化回调：执行@PostConstruct等注解的方法以及其他初始化回调。
     * 应用BeanPostProcessor：执行所有注册的BeanPostProcessor的postProcessBeforeInitialization和postProcessAfterInitialization方法。
     *
     * @param beanClass the class of the bean to create
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     */
    <T> T createBean(Class<T> beanClass) throws BeansException;

    /**
     * Populate the given bean instance through applying after-instantiation callbacks
     * and bean property post-processing (e.g. for annotation-driven injection).
     * <p>Note: This is essentially intended for (re-)populating annotated fields and
     * methods, either for new instances or for deserialized instances. It does
     * <i>not</i> imply traditional by-name or by-type autowiring of properties;
     * use {@link #autowireBeanProperties} for those purposes.
     * 这段注释描述了一个方法，该方法用于通过应用实例化后的回调和bean属性的后期处理来填充给定的bean实例。这个方法主要用于处理带有注解的字段和方法，而不是传统意义上的按名称或按类型的自动装配。
     *
     * 以下是这段注释的详细解释：
     *
     * 主要功能
     * 填充bean实例：通过应用实例化后的回调和bean属性的后期处理来填充给定的bean实例。这里的“填充”指的是对bean进行进一步的配置或初始化。
     * 实现细节
     * 实例化后的回调：在bean实例创建之后，可以应用一些回调操作来进一步处理这个bean。
     * 属性后期处理：对bean的属性进行后期处理，例如处理带有注解的字段和方法。
     * 特点
     * 处理注解：这个方法主要用于处理带有注解的字段和方法，既可以应用于新创建的实例，也可以应用于反序列化后的实例。
     * 不涉及传统自动装配：这个方法不涉及传统意义上的按名称或按类型的自动装配。如果需要进行这种自动装配，请使用autowireBeanProperties方法。
     * 使用场景
     * 这个方法适用于以下场景：
     *
     * 处理注解：填充带有注解的字段和方法，例如通过@Autowired注解进行依赖注入。
     * 实例化后的处理：在bean实例化之后，执行一些回调操作来进一步配置或初始化这个bean。
     * 反序列化后的处理：在反序列化一个bean实例后，对其进行填充和配置。
     *
     * @param existingBean the existing bean instance
     * @throws BeansException if wiring failed
     */
    void autowireBean(Object existingBean) throws BeansException;

    /**
     * Configure the given raw bean: autowiring bean properties, applying
     * bean property values, applying factory callbacks such as {@code setBeanName}
     * and {@code setBeanFactory}, and also applying all bean post processors
     * (including ones which might wrap the given raw bean).
     * <p>This is effectively a superset of what {@link #initializeBean} provides,
     * fully applying the configuration specified by the corresponding bean definition.
     * <b>Note: This method requires a bean definition for the given name!</b>
     *
     * 这段注释描述了一个方法，该方法用于完全配置一个给定的原始bean实例。这个方法不仅会对bean进行自动装配，
     * 还会应用bean属性值、执行工厂回调（如设置bean名称和bean工厂）、以及应用所有bean后置处理器（这些处理器可能会包装给定的原始bean）。
     *
     * 以下是这段注释的详细解释：
     *
     * 主要功能
     * 完全配置bean：这个方法负责对给定的原始bean进行全面配置，包括但不限于：
     * 自动装配bean的属性；
     * 应用bean属性值；
     * 执行工厂回调（如setBeanName和setBeanFactory）；
     * 应用所有bean后置处理器（BeanPostProcessor）。
     * 实现细节
     * 全面配置：这个方法实际上提供了比initializeBean方法更多的功能，它会完全应用由对应的bean定义指定的配置。
     * 依赖bean定义：值得注意的是，这个方法需要一个对应名字的bean定义！
     * 使用场景
     * 这个方法适用于以下场景：
     *
     * bean的完全初始化：当需要确保一个bean被完全初始化，并且所有配置都被正确应用时。
     * 自动装配和属性设置：确保bean的属性被自动装配，并且所有配置好的属性值被应用。
     * 执行工厂回调：执行诸如设置bean名称和bean工厂等工厂回调。
     * 应用后置处理器：确保所有已注册的BeanPostProcessor都能够对bean进行处理，甚至可能会包装原始bean。
     *
     * @param existingBean the existing bean instance
     * @param beanName     the name of the bean, to be passed to it if necessary
     *                     (a bean definition of that name has to be available)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
     * @throws BeansException                if the initialization failed
     * @see #initializeBean
     */
    Object configureBean(Object existingBean, String beanName) throws BeansException;


    //-------------------------------------------------------------------------
    // TODO: Specialized methods for fine-grained control over the bean lifecycle
    // 细粒度控制：提供一组专门的方法，允许对bean的生命周期进行详细的控制
    //-------------------------------------------------------------------------

    /**
     * Fully create a new bean instance of the given class with the specified
     * autowire strategy. All constants defined in this interface are supported here.
     * <p>Performs full initialization of the bean, including all applicable
     * {@link BeanPostProcessor BeanPostProcessors}. This is effectively a superset
     * of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
     * 该方法用于完全创建一个新的bean实例，并使用指定的自动装配策略（autowire strategy）进行初始化。这个方法不仅创建一个新的bean实例，还会执行bean的完全初始化，包括应用所有适用的BeanPostProcessor。
     *
     * 以下是这段注释的详细解释：TODO
     *
     * 主要功能
     * 完全创建新的bean实例：创建一个新的bean实例，并使用指定的自动装配策略来进行初始化。
     * 支持所有自动装配常量：支持在这个接口中定义的所有自动装配常量。
     * 初始化步骤
     * 完全初始化：执行bean的完全初始化，包括应用所有适用的BeanPostProcessor。
     * 超集功能：这个方法实际上是autowire方法的超集，增加了initializeBean的行为。
     *
     * @param beanClass       the class of the bean to create
     * @param autowireMode    by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     *                        (not applicable to autowiring a constructor, thus ignored there)
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     */
    Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Instantiate a new bean instance of the given class with the specified autowire
     * strategy. All constants defined in this interface are supported here.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * before-instantiation callbacks (e.g. for annotation-driven injection).
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the construction of the instance.
     *
     * @param beanClass       the class of the bean to instantiate
     * @param autowireMode    by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     *                        references in the bean instance (not applicable to autowiring a constructor,
     *                        thus ignored there)
     * @return the new bean instance
     * @throws BeansException if instantiation or wiring failed
     * @see #AUTOWIRE_NO
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_CONSTRUCTOR
     * @see #AUTOWIRE_AUTODETECT
     * @see #initializeBean
     * @see #applyBeanPostProcessorsBeforeInitialization
     * @see #applyBeanPostProcessorsAfterInitialization
     */
    Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

    /**
     * Autowire the bean properties of the given bean instance by name or type.
     * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
     * after-instantiation callbacks (e.g. for annotation-driven injection).
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the configuration of the instance.
     *
     * @param existingBean    the existing bean instance
     * @param autowireMode    by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for object
     *                        references in the bean instance
     * @throws BeansException if wiring failed
     * @see #AUTOWIRE_BY_NAME
     * @see #AUTOWIRE_BY_TYPE
     * @see #AUTOWIRE_NO
     */
    void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
            throws BeansException;

    /**
     * Apply the property values of the bean definition with the given name to
     * the given bean instance. The bean definition can either define a fully
     * self-contained bean, reusing its property values, or just property values
     * meant to be used for existing bean instances.
     * <p>This method does <i>not</i> autowire bean properties; it just applies
     * explicitly defined property values. Use the {@link #autowireBeanProperties}
     * method to autowire an existing bean instance.
     * <b>Note: This method requires a bean definition for the given name!</b>
     * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
     * callbacks or perform any further initialization of the bean. This interface
     * offers distinct, fine-grained operations for those purposes, for example
     * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
     * callbacks are applied, if applicable to the configuration of the instance.
     *
     * @param existingBean the existing bean instance
     * @param beanName     the name of the bean definition in the bean factory
     *                     (a bean definition of that name has to be available)
     * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
     * @throws BeansException                if applying the property values failed
     * @see #autowireBeanProperties
     */
    void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

    /**
     * Initialize the given raw bean, applying factory callbacks
     * such as {@code setBeanName} and {@code setBeanFactory},
     * also applying all bean post processors (including ones which
     * might wrap the given raw bean).
     * <p>Note that no bean definition of the given name has to exist
     * in the bean factory. The passed-in bean name will simply be used
     * for callbacks but not checked against the registered bean definitions.
     * 主要功能
     * 初始化原始bean：初始化一个尚未完全配置的bean实例，包括执行一系列的初始化操作。
     * 初始化步骤
     * 应用工厂回调：执行一些工厂回调方法，如setBeanName和setBeanFactory。
     * 应用bean后置处理器：应用所有注册的bean后置处理器，这些处理器可能会对bean进行进一步的包装或修改。
     * 特点
     * 无需检查bean定义：该方法不需要在bean工厂中存在给定名称的bean定义。传入的bean名称仅用于回调，不会与已注册的bean定义进行校验。
     *
     * @param existingBean the existing bean instance
     * @param beanName     the name of the bean, to be passed to it if necessary
     *                     (only passed to {@link BeanPostProcessor BeanPostProcessors};
     *                     can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to
     *                     enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if the initialization failed
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object initializeBean(Object existingBean, String beanName) throws BeansException;

    /**
     * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
     * instance, invoking their {@code postProcessBeforeInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     *
     * @param existingBean the existing bean instance
     * @param beanName     the name of the bean, to be passed to it if necessary
     *                     (only passed to {@link BeanPostProcessor BeanPostProcessors};
     *                     can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to
     *                     enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessBeforeInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
     * instance, invoking their {@code postProcessAfterInitialization} methods.
     * The returned bean instance may be a wrapper around the original.
     *
     * @param existingBean the existing bean instance
     * @param beanName     the name of the bean, to be passed to it if necessary
     *                     (only passed to {@link BeanPostProcessor BeanPostProcessors};
     *                     can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to
     *                     enforce the given instance to be returned, i.e. no proxies etc)
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException if any post-processing failed
     * @see BeanPostProcessor#postProcessAfterInitialization
     * @see #ORIGINAL_INSTANCE_SUFFIX
     */
    Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

    /**
     * Destroy the given bean instance (typically coming from {@link #createBean}),
     * applying the {@link org.springframework.beans.factory.DisposableBean} contract as well as
     * registered {@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}.
     * <p>Any exception that arises during destruction should be caught
     * and logged instead of propagated to the caller of this method.
     *
     * @param existingBean the bean instance to destroy
     */
    void destroyBean(Object existingBean);


    //-------------------------------------------------------------------------
    // Delegate methods for resolving injection points
    //-------------------------------------------------------------------------

    /**
     * Resolve the bean instance that uniquely matches the given object type, if any,
     * including its bean name.
     * <p>This is effectively a variant of {@link #getBean(Class)} which preserves the
     * bean name of the matching instance.
     *
     * @param requiredType type the bean must match; can be an interface or superclass
     * @return the bean name plus bean instance
     * @throws NoSuchBeanDefinitionException   if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException                  if the bean could not be created
     * @see #getBean(Class)
     * @since 4.3.3
     */
    <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

    /**
     * Resolve a bean instance for the given bean name, providing a dependency descriptor
     * for exposure to target factory methods.
     * <p>This is effectively a variant of {@link #getBean(String, Class)} which supports
     * factory methods with an {@link org.springframework.beans.factory.InjectionPoint}
     * argument.
     *
     * @param name       the name of the bean to look up
     * @param descriptor the dependency descriptor for the requesting injection point
     * @return the corresponding bean instance
     * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
     * @throws BeansException                if the bean could not be created
     * @see #getBean(String, Class)
     * @since 5.1.5
     */
    Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     *
     * @param descriptor         the descriptor for the dependency (field/method/constructor)
     * @param requestingBeanName the name of the bean which declares the given dependency
     * @return the resolved object, or {@code null} if none found
     * @throws NoSuchBeanDefinitionException   if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException                  if dependency resolution failed for any other reason
     * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
     * @since 2.5
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

    /**
     * Resolve the specified dependency against the beans defined in this factory.
     *
     * @param descriptor         the descriptor for the dependency (field/method/constructor)
     * @param requestingBeanName the name of the bean which declares the given dependency
     * @param autowiredBeanNames a Set that all names of autowired beans (used for
     *                           resolving the given dependency) are supposed to be added to
     * @param typeConverter      the TypeConverter to use for populating arrays and collections
     * @return the resolved object, or {@code null} if none found
     * @throws NoSuchBeanDefinitionException   if no matching bean was found
     * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
     * @throws BeansException                  if dependency resolution failed for any other reason
     * @see DependencyDescriptor
     * @since 2.5
     */
    @Nullable
    Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
                             @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
