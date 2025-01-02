/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.beans;

import java.beans.PropertyDescriptor;

/**
 * The central interface of Spring's low-level JavaBeans infrastructure.
 *
 * <p>Typically not used directly but rather implicitly via a
 * {@link org.springframework.beans.factory.BeanFactory} or a
 * {@link org.springframework.validation.DataBinder}.
 *
 * <p>Provides operations to analyze and manipulate standard JavaBeans:
 * the ability to get and set property values (individually or in bulk),
 * get property descriptors, and query the readability/writability of properties.
 *
 * <p>This interface supports <b>nested properties</b> enabling the setting
 * of properties on subproperties to an unlimited depth.
 *
 * <p>A BeanWrapper's default for the "extractOldValueForEditor" setting
 * is "false", to avoid side effects caused by getter method invocations.
 * Turn this to "true" to expose present property values to custom editors.
 *
 * 主要功能
 * 分析和操作JavaBeans：提供获取和设置属性值的能力（单独或批量）、获取属性描述符以及查询属性的可读性和可写性。
 * 支持嵌套属性：允许设置子属性上的属性值，深度不限。
 * 默认设置：BeanWrapper的默认设置为extractOldValueForEditor为false，以避免由于调用getter方法引起的副作用。
 * 如果需要暴露当前属性值给自定义编辑器，则可以将此设置为true。
 * 详细解释
 * 分析和操作JavaBeans
 * BeanWrapper提供了多种操作来处理JavaBeans：
 *
 * 获取和设置属性值：可以通过BeanWrapper来获取或设置JavaBeans的属性值。
 * 批量操作：支持批量设置或获取属性值。
 * 属性描述符：可以获取JavaBeans的属性描述符，这些描述符包含了关于属性的信息，如属性名、类型等。
 * 可读性和可写性：可以查询JavaBeans的属性是否可以读取或写入。
 * 支持嵌套属性
 * BeanWrapper支持嵌套属性，这意味着你可以设置子属性上的属性值，深度不受限制。
 * 这对于处理复杂的对象结构非常有用，比如在对象中有其他对象作为属性的情况。
 *
 * 默认设置
 * BeanWrapper的extractOldValueForEditor属性默认为false。这是因为当编辑器尝试获取旧值时，默认情况下不会调用getter方法，以避免可能产生的副作用。如果需要让编辑器暴露当前属性值，可以将此属性设置为true。
 * 这通常在需要使用自定义编辑器时有用，自定义编辑器可能需要当前的属性值来进行编辑操作。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see PropertyAccessor
 * @see PropertyEditorRegistry
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.validation.BeanPropertyBindingResult
 * @see org.springframework.validation.DataBinder#initBeanPropertyAccess()
 * @since 13 April 2001
 */
public interface BeanWrapper extends ConfigurablePropertyAccessor {

    /**
     * Specify a limit for array and collection auto-growing.
     * <p>Default is unlimited on a plain BeanWrapper.
     *
     * @since 4.1
     */
    void setAutoGrowCollectionLimit(int autoGrowCollectionLimit);

    /**
     * Return the limit for array and collection auto-growing.
     *
     * @since 4.1
     */
    int getAutoGrowCollectionLimit();

    /**
     * Return the bean instance wrapped by this object.
     */
    Object getWrappedInstance();

    /**
     * Return the type of the wrapped bean instance.
     */
    Class<?> getWrappedClass();

    /**
     * Obtain the PropertyDescriptors for the wrapped object
     * (as determined by standard JavaBeans introspection).
     *
     * @return the PropertyDescriptors for the wrapped object
     */
    PropertyDescriptor[] getPropertyDescriptors();

    /**
     * Obtain the property descriptor for a specific property
     * of the wrapped object.
     *
     * @param propertyName the property to obtain the descriptor for
     *                     (may be a nested path, but not an indexed/mapped property)
     * @return the property descriptor for the specified property
     * @throws InvalidPropertyException if there is no such property
     */
    PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;

}
