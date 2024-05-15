/*
 *
 *  * Copyright 2017 The OpenDSP Project
 *  *
 *  * The OpenDSP Project licenses this file to you under the Apache License,
 *  * version 2.0 (the "License"); you may not use this file except in compliance
 *  * with the License. You may obtain a copy of the License at:
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *
 */
package ai.houyi.dorado.rest.router.trie;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import ai.houyi.dorado.rest.annotation.Controller;
import ai.houyi.dorado.rest.annotation.HttpMethod;
import ai.houyi.dorado.rest.annotation.Path;
import ai.houyi.dorado.rest.util.StringUtils;

/**
 * @author wangwp
 */
public class UriRoutingRegistry {

    private static final UriRoutingRegistry _instance = new UriRoutingRegistry();
    private static final Router trieRouter = Router.getInstance();

    private UriRoutingRegistry() {
    }

    public static UriRoutingRegistry getInstance() {
        return _instance;
    }

    public void register(Class<?> type) {
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
            trieRouter.addRoute(new Route(requestPath,
                    httpMethod == null ? null : httpMethod.value(),
                    RouteHandler.create(method)));
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
}
