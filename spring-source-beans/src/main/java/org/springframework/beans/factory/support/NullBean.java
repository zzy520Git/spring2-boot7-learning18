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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;

/**
 * Internal representation of a null bean instance, e.g. for a {@code null} value
 * returned from {@link FactoryBean#getObject()} or from a factory method.
 *
 * <p>Each such null bean is represented by a dedicated {@code NullBean} instance
 * which are not equal to each other, uniquely differentiating each bean as returned
 * from all variants of {@link org.springframework.beans.factory.BeanFactory#getBean}.
 * However, each such instance will return {@code true} for {@code #equals(null)}
 * and returns "null" from {@code #toString()}, which is how they can be tested
 * externally (since this class itself is not public).
 * 主要功能
 * 内部表示：对于那些返回null值的bean，Spring框架内部使用一个专门的NullBean实例来表示。
 * 唯一性：每个NullBean实例都是唯一的，彼此不相等，这样可以区分通过各种BeanFactory#getBean方法返回的不同bean实例。
 * 外部测试：尽管每个NullBean实例都是唯一的，但它们都会返回true对于equals(null)的比较，
 * 并且它们的toString()方法返回字符串"null"。这是外部测试这些NullBean实例的方式（因为这个类本身不是公开的）。
 *
 * 详细解释
 * 内部表示
 * FactoryBean返回null：当一个FactoryBean的getObject()方法返回null时，Spring框架会使用一个特殊的NullBean实例来表示这个null值。
 * 工厂方法返回null：同样地，当一个工厂方法返回null时，也会使用NullBean来表示。
 * 唯一性
 * 不同的NullBean实例：每个NullBean实例都是唯一的，这意味着即使它们代表的是null值，它们仍然被视为不同的对象。这种设计可以确保通过不同的方式获取到的null值的bean可以被正确地区分开来。
 * 不相等性：虽然每个NullBean实例都表示null值，但是它们彼此之间是不相等的（equals方法返回false），除非它们是同一个实例。
 * 外部测试
 * equals(null)返回true：每个NullBean实例的equals(null)方法都会返回true，这表明它们可以与null进行相等性的比较。
 * toString()返回"null"：每个NullBean实例的toString()方法返回字符串"null"，这使得可以通过字符串表示来识别它们。
 *
 * @author Juergen Hoeller
 * @since 5.0
 */
final class NullBean {

    NullBean() {
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return (this == obj || obj == null);
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }

}
