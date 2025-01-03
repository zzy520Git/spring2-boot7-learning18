/*
 * Copyright 2002-2023 the original author or authors.
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

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A root bean definition represents the merged bean definition that backs
 * a specific bean in a Spring BeanFactory at runtime. It might have been created
 * from multiple original bean definitions that inherit from each other,
 * typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 *
 * <p>Root bean definitions may also be used for registering individual bean definitions
 * in the configuration phase. However, since Spring 2.5, the preferred way to register
 * bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define
 * parent dependencies, not 'hard-coding' the role as a root bean definition.
 *
 * 这段描述介绍了一个 RootBeanDefinition 类，它代表了 Spring BeanFactory 中某个特定 Bean 的合并后的定义。
 * RootBeanDefinition 可以理解为运行时针对一个 Bean 的最终定义视图，它可能包含了多个继承自彼此的原始 Bean 定义信息。
 *
 * RootBeanDefinition 的特点
 * 合并后的定义：
 * RootBeanDefinition 表示的是合并后的 Bean 定义，这可能包含了来自多个层次的定义信息。例如，当一个 Bean 继承自另一个 Bean 时，
 * 最终的 RootBeanDefinition 会包含所有层次上的定义信息。
 * 最终的定义视图：
 * 在运行时，RootBeanDefinition 提供了针对某个 Bean 的统一视图，包含了所有必要的配置信息。
 * 注册 Bean 定义：
 * RootBeanDefinition 也可以用来在配置阶段注册单独的 Bean 定义。然而，从 Spring 2.5 开始，
 * 更推荐使用 GenericBeanDefinition 类来注册 Bean 定义，因为 GenericBeanDefinition 具有更大的灵活性。
 * RootBeanDefinition 与 GenericBeanDefinition 的区别
 * 硬编码的角色：
 * RootBeanDefinition 通常用于表示最终的合并定义，而 GenericBeanDefinition 则更倾向于动态定义 Bean 的父依赖关系，避免将角色硬编码为根 Bean 定义。
 * 动态定义父依赖：
 * GenericBeanDefinition 允许动态定义父依赖关系，这意味着它可以在运行时更改父 Bean 的定义，而 RootBeanDefinition 则通常表示已经完成合并的最终定义。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

    @Nullable
    private BeanDefinitionHolder decoratedDefinition;

    @Nullable
    private AnnotatedElement qualifiedElement;

    /**
     * Determines if the definition needs to be re-merged.
     */
    volatile boolean stale;
    /**
     * 决定这个RootBeanDefinition的Class是否可以被缓存起来服用
     * 因为Class由不同的ClassLoader加载是不能复用的
     * TODO：尤其在一些三方框架中，
     */
    boolean allowCaching = true;
    /**
     * TODO:
     */
    boolean isFactoryMethodUnique;

    @Nullable
    volatile ResolvableType targetType;

    /**
     * TODO Package-visible field for caching the determined Class of a given bean definition.
     * 解析之后的Bean的Class类型
     */
    @Nullable
    volatile Class<?> resolvedTargetType;

    /**
     * Package-visible field for caching if the bean is a factory bean.
     */
    @Nullable
    volatile Boolean isFactoryBean;

    /**
     * Package-visible field for caching the return type of a generically typed factory method.
     */
    @Nullable
    volatile ResolvableType factoryMethodReturnType;

    /**
     * Package-visible field for caching a unique factory method candidate for introspection.
     */
    @Nullable
    volatile Method factoryMethodToIntrospect;

    /**
     * Package-visible field for caching a resolved destroy method name (also for inferred).
     */
    @Nullable
    volatile String resolvedDestroyMethodName;

    /**
     * Common lock for the four constructor fields below.
     */
    final Object constructorArgumentLock = new Object();

    /**
     * Package-visible field for caching the resolved constructor or factory method.
     */
    @Nullable
    Executable resolvedConstructorOrFactoryMethod;

    /**
     * Package-visible field that marks the constructor arguments as resolved.
     */
    boolean constructorArgumentsResolved = false;

    /**
     * Package-visible field for caching fully resolved constructor arguments.
     */
    @Nullable
    Object[] resolvedConstructorArguments;

    /**
     * Package-visible field for caching partly prepared constructor arguments.
     */
    @Nullable
    Object[] preparedConstructorArguments;

    /**
     * Common lock for the two post-processing fields below.
     */
    final Object postProcessingLock = new Object();

    /**
     * Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied.
     */
    boolean postProcessed = false;

    /**
     * Package-visible field that indicates a before-instantiation post-processor having kicked in.
     */
    @Nullable
    volatile Boolean beforeInstantiationResolved;

    @Nullable
    private Set<Member> externallyManagedConfigMembers;

    @Nullable
    private Set<String> externallyManagedInitMethods;

    @Nullable
    private Set<String> externallyManagedDestroyMethods;


    /**
     * Create a new RootBeanDefinition, to be configured through its bean
     * properties and configuration methods.
     *
     * @see #setBeanClass
     * @see #setScope
     * @see #setConstructorArgumentValues
     * @see #setPropertyValues
     */
    public RootBeanDefinition() {
        super();
    }

    /**
     * Create a new RootBeanDefinition for a singleton.
     *
     * @param beanClass the class of the bean to instantiate
     * @see #setBeanClass
     */
    public RootBeanDefinition(@Nullable Class<?> beanClass) {
        super();
        setBeanClass(beanClass);
    }

    /**
     * Create a new RootBeanDefinition for a singleton bean, constructing each instance
     * through calling the given supplier (possibly a lambda or method reference).
     *
     * @param beanClass        the class of the bean to instantiate
     * @param instanceSupplier the supplier to construct a bean instance,
     *                         as an alternative to a declaratively specified factory method
     * @see #setInstanceSupplier
     * @since 5.0
     */
    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
        super();
        setBeanClass(beanClass);
        setInstanceSupplier(instanceSupplier);
    }

    /**
     * Create a new RootBeanDefinition for a scoped bean, constructing each instance
     * through calling the given supplier (possibly a lambda or method reference).
     *
     * @param beanClass        the class of the bean to instantiate
     * @param scope            the name of the corresponding scope
     * @param instanceSupplier the supplier to construct a bean instance,
     *                         as an alternative to a declaratively specified factory method
     * @see #setInstanceSupplier
     * @since 5.0
     */
    public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
        super();
        setBeanClass(beanClass);
        setScope(scope);
        setInstanceSupplier(instanceSupplier);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * using the given autowire mode.
     *
     * @param beanClass       the class of the bean to instantiate
     * @param autowireMode    by name or type, using the constants in this interface
     * @param dependencyCheck whether to perform a dependency check for objects
     *                        (not applicable to autowiring a constructor, thus ignored there)
     */
    public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
        super();
        setBeanClass(beanClass);
        setAutowireMode(autowireMode);
        if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
            setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
        }
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     *
     * @param beanClass the class of the bean to instantiate
     * @param cargs     the constructor argument values to apply
     * @param pvs       the property values to apply
     */
    public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs,
                              @Nullable MutablePropertyValues pvs) {

        super(cargs, pvs);
        setBeanClass(beanClass);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * <p>Takes a bean class name to avoid eager loading of the bean class.
     *
     * @param beanClassName the name of the class to instantiate
     */
    public RootBeanDefinition(String beanClassName) {
        setBeanClassName(beanClassName);
    }

    /**
     * Create a new RootBeanDefinition for a singleton,
     * providing constructor arguments and property values.
     * <p>Takes a bean class name to avoid eager loading of the bean class.
     *
     * @param beanClassName the name of the class to instantiate
     * @param cargs         the constructor argument values to apply
     * @param pvs           the property values to apply
     */
    public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        setBeanClassName(beanClassName);
    }

    /**
     * Create a new RootBeanDefinition as deep copy of the given
     * bean definition.
     *
     * @param original the original bean definition to copy from
     */
    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.decoratedDefinition = original.decoratedDefinition;
        this.qualifiedElement = original.qualifiedElement;
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
        this.targetType = original.targetType;
        this.factoryMethodToIntrospect = original.factoryMethodToIntrospect;
    }

    /**
     * Create a new RootBeanDefinition as deep copy of the given
     * bean definition.
     *
     * @param original the original bean definition to copy from
     */
    RootBeanDefinition(BeanDefinition original) {
        super(original);
    }


    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public void setParentName(@Nullable String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
        }
    }

    /**
     * Register a target definition that is being decorated by this bean definition.
     */
    public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
        this.decoratedDefinition = decoratedDefinition;
    }

    /**
     * Return the target definition that is being decorated by this bean definition, if any.
     */
    @Nullable
    public BeanDefinitionHolder getDecoratedDefinition() {
        return this.decoratedDefinition;
    }

    /**
     * Specify the {@link AnnotatedElement} defining qualifiers,
     * to be used instead of the target class or factory method.
     *
     * @see #setTargetType(ResolvableType)
     * @see #getResolvedFactoryMethod()
     * @since 4.3.3
     */
    public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
        this.qualifiedElement = qualifiedElement;
    }

    /**
     * Return the {@link AnnotatedElement} defining qualifiers, if any.
     * Otherwise, the factory method and target class will be checked.
     *
     * @since 4.3.3
     */
    @Nullable
    public AnnotatedElement getQualifiedElement() {
        return this.qualifiedElement;
    }

    /**
     * Specify a generics-containing target type of this bean definition, if known in advance.
     *
     * @since 4.3.3
     */
    public void setTargetType(ResolvableType targetType) {
        this.targetType = targetType;
    }

    /**
     * Specify the target type of this bean definition, if known in advance.
     *
     * @since 3.2.2
     */
    public void setTargetType(@Nullable Class<?> targetType) {
        this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
    }

    /**
     * Return the target type of this bean definition, if known
     * (either specified in advance or resolved on first instantiation).
     *
     * @since 3.2.2
     */
    @Nullable
    public Class<?> getTargetType() {
        if (this.resolvedTargetType != null) {
            return this.resolvedTargetType;
        }
        ResolvableType targetType = this.targetType;
        return (targetType != null ? targetType.resolve() : null);
    }

    /**
     * Return a {@link ResolvableType} for this bean definition,
     * either from runtime-cached type information or from configuration-time
     * {@link #setTargetType(ResolvableType)} or {@link #setBeanClass(Class)},
     * also considering resolved factory method definitions.
     *
     * @see #setTargetType(ResolvableType)
     * @see #setBeanClass(Class)
     * @see #setResolvedFactoryMethod(Method)
     * @since 5.1
     */
    @Override
    public ResolvableType getResolvableType() {
        ResolvableType targetType = this.targetType;
        if (targetType != null) {
            return targetType;
        }
        ResolvableType returnType = this.factoryMethodReturnType;
        if (returnType != null) {
            return returnType;
        }
        Method factoryMethod = this.factoryMethodToIntrospect;
        if (factoryMethod != null) {
            return ResolvableType.forMethodReturnType(factoryMethod);
        }
        return super.getResolvableType();
    }

    /**
     * Determine preferred constructors to use for default construction, if any.
     * Constructor arguments will be autowired if necessary.
     *
     * @return one or more preferred constructors, or {@code null} if none
     * (in which case the regular no-arg default constructor will be called)
     * @since 5.1
     */
    @Nullable
    public Constructor<?>[] getPreferredConstructors() {
        return null;
    }

    /**
     * Specify a factory method name that refers to a non-overloaded method.
     */
    public void setUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = true;
    }

    /**
     * Specify a factory method name that refers to an overloaded method.
     *
     * @since 5.2
     */
    public void setNonUniqueFactoryMethodName(String name) {
        Assert.hasText(name, "Factory method name must not be empty");
        setFactoryMethodName(name);
        this.isFactoryMethodUnique = false;
    }

    /**
     * Check whether the given candidate qualifies as a factory method.
     */
    public boolean isFactoryMethod(Method candidate) {
        return candidate.getName().equals(getFactoryMethodName());
    }

    /**
     * Set a resolved Java Method for the factory method on this bean definition.
     *
     * @param method the resolved factory method, or {@code null} to reset it
     * @since 5.2
     */
    public void setResolvedFactoryMethod(@Nullable Method method) {
        this.factoryMethodToIntrospect = method;
    }

    /**
     * Return the resolved factory method as a Java Method object, if available.
     *
     * @return the factory method, or {@code null} if not found or not resolved yet
     */
    @Nullable
    public Method getResolvedFactoryMethod() {
        return this.factoryMethodToIntrospect;
    }

    /**
     * Register an externally managed configuration method or field.
     */
    public void registerExternallyManagedConfigMember(Member configMember) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedConfigMembers == null) {
                this.externallyManagedConfigMembers = new LinkedHashSet<>(1);
            }
            this.externallyManagedConfigMembers.add(configMember);
        }
    }

    /**
     * Determine if the given method or field is an externally managed configuration member.
     */
    public boolean isExternallyManagedConfigMember(Member configMember) {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedConfigMembers != null &&
                    this.externallyManagedConfigMembers.contains(configMember));
        }
    }

    /**
     * Get all externally managed configuration methods and fields (as an immutable Set).
     *
     * @since 5.3.11
     */
    public Set<Member> getExternallyManagedConfigMembers() {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedConfigMembers != null ?
                    Collections.unmodifiableSet(new LinkedHashSet<>(this.externallyManagedConfigMembers)) :
                    Collections.emptySet());
        }
    }

    /**
     * Register an externally managed configuration initialization method &mdash;
     * for example, a method annotated with JSR-250's
     * {@link javax.annotation.PostConstruct} annotation.
     * <p>The supplied {@code initMethod} may be the
     * {@linkplain Method#getName() simple method name} for non-private methods or the
     * {@linkplain org.springframework.util.ClassUtils#getQualifiedMethodName(Method)
     * qualified method name} for {@code private} methods. A qualified name is
     * necessary for {@code private} methods in order to disambiguate between
     * multiple private methods with the same name within a class hierarchy.
     */
    public void registerExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedInitMethods == null) {
                this.externallyManagedInitMethods = new LinkedHashSet<>(1);
            }
            this.externallyManagedInitMethods.add(initMethod);
        }
    }

    /**
     * Determine if the given method name indicates an externally managed
     * initialization method.
     * <p>See {@link #registerExternallyManagedInitMethod} for details
     * regarding the format for the supplied {@code initMethod}.
     */
    public boolean isExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedInitMethods != null &&
                    this.externallyManagedInitMethods.contains(initMethod));
        }
    }

    /**
     * Determine if the given method name indicates an externally managed
     * initialization method, regardless of method visibility.
     * <p>In contrast to {@link #isExternallyManagedInitMethod(String)}, this
     * method also returns {@code true} if there is a {@code private} externally
     * managed initialization method that has been
     * {@linkplain #registerExternallyManagedInitMethod(String) registered}
     * using a qualified method name instead of a simple method name.
     *
     * @since 5.3.17
     */
    boolean hasAnyExternallyManagedInitMethod(String initMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedInitMethod(initMethod)) {
                return true;
            }
            return hasAnyExternallyManagedMethod(this.externallyManagedInitMethods, initMethod);
        }
    }

    /**
     * Get all externally managed initialization methods (as an immutable Set).
     * <p>See {@link #registerExternallyManagedInitMethod} for details
     * regarding the format for the initialization methods in the returned set.
     *
     * @since 5.3.11
     */
    public Set<String> getExternallyManagedInitMethods() {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedInitMethods != null ?
                    Collections.unmodifiableSet(new LinkedHashSet<>(this.externallyManagedInitMethods)) :
                    Collections.emptySet());
        }
    }

    /**
     * Register an externally managed configuration destruction method &mdash;
     * for example, a method annotated with JSR-250's
     * {@link javax.annotation.PreDestroy} annotation.
     * <p>The supplied {@code destroyMethod} may be the
     * {@linkplain Method#getName() simple method name} for non-private methods or the
     * {@linkplain org.springframework.util.ClassUtils#getQualifiedMethodName(Method)
     * qualified method name} for {@code private} methods. A qualified name is
     * necessary for {@code private} methods in order to disambiguate between
     * multiple private methods with the same name within a class hierarchy.
     */
    public void registerExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (this.externallyManagedDestroyMethods == null) {
                this.externallyManagedDestroyMethods = new LinkedHashSet<>(1);
            }
            this.externallyManagedDestroyMethods.add(destroyMethod);
        }
    }

    /**
     * Determine if the given method name indicates an externally managed
     * destruction method.
     * <p>See {@link #registerExternallyManagedDestroyMethod} for details
     * regarding the format for the supplied {@code destroyMethod}.
     */
    public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedDestroyMethods != null &&
                    this.externallyManagedDestroyMethods.contains(destroyMethod));
        }
    }

    /**
     * Determine if the given method name indicates an externally managed
     * destruction method, regardless of method visibility.
     * <p>In contrast to {@link #isExternallyManagedDestroyMethod(String)}, this
     * method also returns {@code true} if there is a {@code private} externally
     * managed destruction method that has been
     * {@linkplain #registerExternallyManagedDestroyMethod(String) registered}
     * using a qualified method name instead of a simple method name.
     *
     * @since 5.3.17
     */
    boolean hasAnyExternallyManagedDestroyMethod(String destroyMethod) {
        synchronized (this.postProcessingLock) {
            if (isExternallyManagedDestroyMethod(destroyMethod)) {
                return true;
            }
            return hasAnyExternallyManagedMethod(this.externallyManagedDestroyMethods, destroyMethod);
        }
    }

    private static boolean hasAnyExternallyManagedMethod(Set<String> candidates, String methodName) {
        if (candidates != null) {
            for (String candidate : candidates) {
                int indexOfDot = candidate.lastIndexOf('.');
                if (indexOfDot > 0) {
                    String candidateMethodName = candidate.substring(indexOfDot + 1);
                    if (candidateMethodName.equals(methodName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get all externally managed destruction methods (as an immutable Set).
     * <p>See {@link #registerExternallyManagedDestroyMethod} for details
     * regarding the format for the destruction methods in the returned set.
     *
     * @since 5.3.11
     */
    public Set<String> getExternallyManagedDestroyMethods() {
        synchronized (this.postProcessingLock) {
            return (this.externallyManagedDestroyMethods != null ?
                    Collections.unmodifiableSet(new LinkedHashSet<>(this.externallyManagedDestroyMethods)) :
                    Collections.emptySet());
        }
    }


    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
    }

    @Override
    public String toString() {
        return "Root bean: " + super.toString();
    }

}
