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

import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;

/**
 * Interface that encapsulates configuration methods for a PropertyAccessor.
 * Also extends the PropertyEditorRegistry interface, which defines methods
 * for PropertyEditor management.
 *
 * <p>Serves as base interface for {@link BeanWrapper}.
 *
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @see BeanWrapper
 * @since 2.0
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {

    /**
     * Specify a Spring 3.0 ConversionService to use for converting
     * property values, as an alternative to JavaBeans PropertyEditors.
     *
     * 在 Spring 框架中，ConversionService 接口及其实现提供了类型转换的功能，它允许在不同类型之间进行转换，
     * 使得类型转换变得更加灵活和统一。ConversionService 是 Spring 3.0 引入的一个新特性，旨在为类型转换提供一个集中管理和扩展的机制。
     *
     * ConversionService 的作用
     * 类型转换：
     * ConversionService 支持在不同数据类型之间进行转换，这对于处理来自用户输入的数据、从数据库读取的数据或者从其他系统接收的数据时非常有用。
     * 例如，可以将字符串转换为日期、数字或其他复杂对象。
     * 统一的类型转换机制：
     * 通过 ConversionService，可以在整个应用中使用统一的类型转换策略。这有助于避免在不同地方使用不同的转换逻辑，从而减少代码的冗余并提高一致性。
     * 扩展性：
     * ConversionService 可以轻松扩展，支持自定义类型转换器的注册。开发人员可以根据需要添加新的转换器，以支持更多的数据类型转换。
     * 与 Spring 组件的集成：
     * ConversionService 可以与 Spring MVC、Spring Data Bindings、Spring Data JPA 等组件集成，
     * 为这些组件提供类型转换的能力。例如，在 Spring MVC 中，ConversionService 可以用来将 HTTP 请求参数转换为目标对象。
     */
    void setConversionService(@Nullable ConversionService conversionService);

    /**
     * Return the associated ConversionService, if any.
     */
    @Nullable
    ConversionService getConversionService();

    /**
     * Set whether to extract the old property value when applying a
     * property editor to a new value for a property.
     */
    void setExtractOldValueForEditor(boolean extractOldValueForEditor);

    /**
     * Return whether to extract the old property value when applying a
     * property editor to a new value for a property.
     */
    boolean isExtractOldValueForEditor();

    /**
     * Set whether this instance should attempt to "auto-grow" a
     * nested path that contains a {@code null} value.
     * <p>If {@code true}, a {@code null} path location will be populated
     * with a default object value and traversed instead of resulting in a
     * {@link NullValueInNestedPathException}.
     * <p>Default is {@code false} on a plain PropertyAccessor instance.
     */
    void setAutoGrowNestedPaths(boolean autoGrowNestedPaths);

    /**
     * Return whether "auto-growing" of nested paths has been activated.
     */
    boolean isAutoGrowNestedPaths();

}
