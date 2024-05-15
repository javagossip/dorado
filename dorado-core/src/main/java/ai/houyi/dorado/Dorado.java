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
package ai.houyi.dorado;

import java.net.URL;

import ai.houyi.dorado.rest.server.DoradoServerBuilder;
import ai.houyi.dorado.rest.util.ClassLoaderUtils;

/**
 * @author wangwp
 */
public final class Dorado {

    public static volatile ClassLoader classLoader;
    public static volatile boolean springInitialized;
    public static volatile BeanContainer beanContainer;
    public static volatile DoradoServerBuilder serverConfig;
    public static volatile Class<?> mainClass;

    public static volatile boolean isEnableSwagger;
    public static volatile boolean isEnableSpring;
    public static volatile boolean isEnableProtobuf;
    public static volatile boolean isEnableSwaggerUi;

    static {
        classLoader = Thread.currentThread().getContextClassLoader();
        beanContainer = BeanContainer.DEFAULT;

        try {
            Class.forName("com.google.protobuf.Message");
            isEnableProtobuf = true;
        } catch (Throwable ex) {
            // ignore this ex
        }

        try {
            Class.forName("org.springframework.context.ApplicationContext");
            isEnableSpring = true;
        } catch (Throwable ex) {
            // ignore this ex
        }

        try {
            Class.forName("mobi.f2time.dorado.swagger.controller.SwaggerV2Controller");
            isEnableSwagger = true;
        } catch (Throwable ex) {
            // ignore this ex
        }

        try {
            URL url = ClassLoaderUtils.getURL("META-INF/webjars/swagger-ui");
            isEnableSwaggerUi = url != null;
        } catch (Throwable ex) {
            //ignore this ex
        }
    }
}
