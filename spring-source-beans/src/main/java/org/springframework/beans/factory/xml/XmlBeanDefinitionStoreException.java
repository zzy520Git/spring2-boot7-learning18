/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XML-specific BeanDefinitionStoreException subclass that wraps a
 * {@link SAXException}, typically a {@link SAXParseException}
 * which contains information about the error location.
 *
 * @author Juergen Hoeller
 * @see #getLineNumber()
 * @see SAXParseException
 * @since 2.0.2
 */
@SuppressWarnings("serial")
public class XmlBeanDefinitionStoreException extends BeanDefinitionStoreException {

    /**
     * Create a new XmlBeanDefinitionStoreException.
     *
     * @param resourceDescription description of the resource that the bean definition came from
     * @param msg                 the detail message (used as exception message as-is)
     * @param cause               the SAXException (typically a SAXParseException) root cause
     * @see SAXParseException
     */
    public XmlBeanDefinitionStoreException(String resourceDescription, String msg, SAXException cause) {
        super(resourceDescription, msg, cause);
    }

    /**
     * Return the line number in the XML resource that failed.
     *
     * @return the line number if available (in case of a SAXParseException); -1 else
     * @see SAXParseException#getLineNumber()
     */
    public int getLineNumber() {
        Throwable cause = getCause();
        if (cause instanceof SAXParseException) {
            return ((SAXParseException) cause).getLineNumber();
        }
        return -1;
    }

}
