/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.ResourceRegister;
import ai.houyi.dorado.rest.ResourceRegisters;
import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.ExceptionAdvice;
import ai.houyi.dorado.rest.annotation.FilterPath;
import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.controller.RootController;
import ai.houyi.dorado.rest.http.Filter;
import ai.houyi.dorado.rest.http.MethodReturnValueHandler;
import ai.houyi.dorado.rest.http.MethodReturnValueHandlerConfig;
import ai.houyi.dorado.rest.router.DoradoRouteHandler;
import ai.houyi.dorado.rest.router.Router;
import ai.houyi.dorado.rest.util.PackageScanner;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * @author wangwp
 */
public class Webapp {

    private static Webapp webapp;

    private final Set<String> packages;
    private MethodReturnValueHandlerConfig methodReturnValueHandlerConfig;
    private Router router;

    private Webapp(String[] packages) {
        this.packages = new HashSet<>(Arrays.asList(packages));
        this.router = Router.newInstance();
    }

    public static synchronized void create(String[] packages) {
        webapp = new Webapp(packages);
        webapp.initialize();
    }

    public static Webapp get() {
        if (webapp == null) {
            throw new IllegalStateException("webapp not initialized, please create it first");
        }
        return webapp;
    }

    public MethodReturnValueHandlerConfig getMethodReturnValueHandlerConfig() {
        return this.methodReturnValueHandlerConfig;
    }

    public void initialize() {
        Set<Class<?>> classes = new HashSet<>();

        for (ResourceRegister resourceRegister : ResourceRegisters.getInstance().getResourceRegisters()) {
            resourceRegister.register();
        }

        try {
            for (String scanPackage : packages) {
                classes.addAll(PackageScanner.scan(scanPackage));
            }

            registerRoutesByType(RootController.class);
            classes.forEach(this::registerWebComponent);
        } catch (Exception ex) {
            throw new DoradoException(ex);
        }
    }

    private void registerWebComponent(Class<?> type) {
        Annotation exceptionAdvice = type.getAnnotation(ExceptionAdvice.class);
        if (exceptionAdvice != null) {
            registerExceptionAdvice(type);
        }

        if (MethodReturnValueHandler.class.isAssignableFrom(type)) {
            if (this.methodReturnValueHandlerConfig != null) {
                throw new IllegalStateException("Only one instance for [MethodReturnValueHandler] is allowed");
            }

            MethodReturnValueHandler returnValueHandler = (MethodReturnValueHandler) Dorado.beanContainer.getBean(type);
            this.methodReturnValueHandlerConfig = new MethodReturnValueHandlerConfig(returnValueHandler);
        }

        if (Filter.class.isAssignableFrom(type)) {
            FilterPath filterPath = type.getAnnotation(FilterPath.class);
            if (filterPath == null) {
                return;
            }

            String[] include = filterPath.include();
            String[] exclude = filterPath.exclude();

            if (include == null && exclude == null) {
                return;
            }

            FilterConfiguration.Builder filterConfigurationBuilder = FilterConfiguration.builder();
            if (include != null && include.length > 0) {
                filterConfigurationBuilder.withPathPatterns(Arrays.asList(include));
            }

            if (exclude != null && exclude.length > 0) {
                filterConfigurationBuilder.withExcludePathPatterns(Arrays.asList(exclude));
            }

            filterConfigurationBuilder.withFilter((Filter) Dorado.beanContainer.getBean(type));
            FilterManager.getInstance().addFilterConfiguration(filterConfigurationBuilder.build());
        } else {
            registerRoutesByType(type);
        }
    }

    private void registerExceptionAdvice(Class<?> type) {
        WebComponents.getInstance().registerExceptionHandlers(type);
    }

    public void registerRoutesByType(Class<?> type) {
        Controller controller = type.getAnnotation(Controller.class);
        if (controller == null) {
            return;
        }

        Path classLevelPath = type.getAnnotation(Path.class);
        String controllerPath = classLevelPath == null ? StringUtils.EMPTY : classLevelPath.value();

        Method[] controllerMethods = type.getDeclaredMethods();
        for (Method method : controllerMethods) {
            if (Modifier.isStatic(method.getModifiers()) || method.getAnnotations().length == 0 ||
                    !Modifier.isPublic(method.getModifiers())) {
                continue;
            }

            Path methodLevelPath = method.getAnnotation(Path.class);
            HttpMethod httpMethod = getHttpMethod(method.getAnnotations());
            String methodPath = methodLevelPath == null ? StringUtils.EMPTY : methodLevelPath.value();

            String requestPath = String.format("%s%s", controllerPath, methodPath);
            router.addRoute(requestPath,
                    httpMethod == null ? null : httpMethod.value(),
                    DoradoRouteHandler.create(method));
        }
    }

    private HttpMethod getHttpMethod(Annotation[] annotations) {
        HttpMethod httpMethod;

        for (Annotation annotation : annotations) {
            httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                return httpMethod;
            }
        }
        return null;
    }

    public Router getRouter() {
        return router;
    }
}
