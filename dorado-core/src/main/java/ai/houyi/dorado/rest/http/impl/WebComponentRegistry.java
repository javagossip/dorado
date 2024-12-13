/*
 * Copyright 2017 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.dorado.rest.http.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.rest.annotation.ExceptionAdvice;
import ai.houyi.dorado.rest.annotation.ExceptionType;

/**
 * @author weiping wang
 */
public final class WebComponentRegistry {

    private static final WebComponentRegistry REGISTRY = new WebComponentRegistry();

    private final ConcurrentMap<Class<? extends Throwable>, ExceptionHandler> exceptionHandlerRegistry =
            new ConcurrentHashMap<>();

    private WebComponentRegistry() {
    }

    public static WebComponentRegistry getWebComponentRegistry() {
        return REGISTRY;
    }

    public ExceptionHandler getExceptionHandler(Class<? extends Throwable> exceptionType) {
        ExceptionHandler handler = exceptionHandlerRegistry.get(exceptionType);

        if (Exception.class.isAssignableFrom(exceptionType)) {
            return getExceptionHandler();
        }

        if (Error.class.isAssignableFrom(exceptionType)) {
            return getErrorHandler();
        }
        return handler;
    }

    private ExceptionHandler getExceptionHandler() {
        ExceptionHandler handler = exceptionHandlerRegistry.get(Exception.class);

        if (handler == null) {
            return exceptionHandlerRegistry.get(Throwable.class);
        }
        return handler;
    }

    private ExceptionHandler getErrorHandler() {
        ExceptionHandler handler = exceptionHandlerRegistry.get(Error.class);
        if (handler == null) {
            return exceptionHandlerRegistry.get(Throwable.class);
        }
        return handler;
    }

    public void registerExceptionHandlers(Class<?> type) {
        Annotation exceptionAdvice = type.getAnnotation(ExceptionAdvice.class);

        if (exceptionAdvice == null) {
            return;
        }

        Object exceptionAdvicor = Dorado.beanContainer.getBean(type);
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            ExceptionType exceptionType = method.getAnnotation(ExceptionType.class);
            if (exceptionType == null) {
                continue;
            }

            ExceptionHandler handler = ExceptionHandler.newExceptionHandler(exceptionAdvicor, method);
            exceptionHandlerRegistry.put(exceptionType.value(), handler);
        }
    }
}
