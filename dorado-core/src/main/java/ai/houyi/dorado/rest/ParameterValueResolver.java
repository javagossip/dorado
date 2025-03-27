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
package ai.houyi.dorado.rest;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSON;

import ai.houyi.dorado.rest.http.Cookie;
import ai.houyi.dorado.rest.http.HttpRequest;
import ai.houyi.dorado.rest.http.HttpResponse;
import ai.houyi.dorado.rest.util.MediaTypeUtils;
import ai.houyi.dorado.rest.util.MethodDescriptor;
import ai.houyi.dorado.rest.util.MethodDescriptor.MethodParameter;
import ai.houyi.dorado.rest.util.TypeConverter;
import ai.houyi.dorado.rest.util.TypeConverters;
import ai.houyi.dorado.rest.util.TypeUtils;
import io.netty.handler.codec.http.HttpMethod;

/**
 * @author wangwp
 */
public interface ParameterValueResolver {

    Object resolveParameterValue(HttpRequest request,
            HttpResponse response,
            MethodDescriptor desc,
            MethodParameter methodParameter,
            String pathVariable);

    @SuppressWarnings({"unchecked", "rawtypes"})
    ParameterValueResolver REQUEST_PARAM = (req, resp, methodDesc, methodParam, pathVariable) -> {
        TypeConverter converter = TypeConverters.resolveConverter(methodParam.getType());
        //如果没有合适的类型转换器且是对象类型的话，尝试将请求参数转换成指定对象
        if (converter == TypeConverter.DUMMY && TypeUtils.isSerializableType(methodParam.getType()) &&
                req.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
            return JSON.parseObject(JSON.toJSONString(req.getParameterMap()), methodParam.getType());
        }
        return converter.convert(req.getParameter(methodParam.getName()));
    };

    @SuppressWarnings("unchecked")
    ParameterValueResolver HEADER_PARAM =
            (req, resp, methodDesc, methodParam, pathVariable) -> TypeConverters.resolveConverter(methodParam.getType())
                    .convert(req.getHeader(methodParam.getName()));

    @SuppressWarnings("unchecked")
    ParameterValueResolver PATH_PARAM =
            (req, resp, methodDesc, methodParam, pathVariable) -> TypeConverters.resolveConverter(methodParam.getType())
                    .convert(pathVariable);

    ParameterValueResolver HTTP_REQUEST =
            (req, resp, methodDesc, methodParam, pathVariable) -> TypeConverter.DUMMY.convert(req);

    ParameterValueResolver HTTP_RESPONSE =
            (req, resp, methodDesc, methodParam, pathVariable) -> TypeConverter.DUMMY.convert(resp);

    @SuppressWarnings("unchecked")
    ParameterValueResolver COOKIE_PARAM = (req, resp, methodDesc, methodParam, pathVariable) -> {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (methodParam.getName().equalsIgnoreCase(cookie.name())) {
                return TypeConverters.resolveConverter(methodParam.getType()).convert(cookie.value());
            }
        }
        Class<?> parameterType = methodParam.getType();
        return parameterType.isPrimitive() ? TypeUtils.primitiveDefault(parameterType) : null;
    };

    @SuppressWarnings({"rawtypes"})
    ParameterValueResolver REQUEST_BODY = (req, resp, methodDesc, methodParam, pathVariable) -> {
        Class<?> parameterType = methodParam.getType();
        InputStream payload = req.getInputStream();
        MessageBodyConverter converter = MessageBodyConverters.getMessageBodyConverter(MediaTypeUtils.defaultForType(
                parameterType,
                methodDesc.consume()));
        Type parameterizedType = methodParam.getParameterizedType();
        return converter.readMessageBody(payload, parameterizedType);
    };

    ParameterValueResolver ALL = new ParameterValueResolver() {
        final List<ParameterValueResolver> resolverList = Arrays.asList(REQUEST_PARAM, PATH_PARAM, HEADER_PARAM);

        @Override
        public Object resolveParameterValue(HttpRequest request,
                HttpResponse response,
                MethodDescriptor desc,
                MethodParameter methodParameter,
                String pathVariable) {
            Class<?> parameterType = methodParameter.getType();
            // 如果没有注解指定参数从何处获取的话，默认按照RequestParam->PathVariable->HeaderParam获取，如果全部没有则返回null
            for (ParameterValueResolver valueResolver : resolverList) {
                Object parameterValue =
                        valueResolver.resolveParameterValue(request, response, desc, methodParameter, pathVariable);
                if (parameterValue != null) {
                    return parameterValue;
                }
            }
            // 如果方法参数是基本类型则必须给定默认值
            if (parameterType.isPrimitive()) {
                return TypeUtils.primitiveDefault(parameterType);
            }

            if (TypeUtils.isWrapper(parameterType)) {
                return null;
            }

            // 如果方法只有一个参数且不是基础类型以及wrapper类型的话尝试利用requestBody转换器
            if (methodParameter.getMethodParameterCount() == 1 &&
                    HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
                return REQUEST_BODY.resolveParameterValue(request, response, desc, methodParameter, pathVariable);
            }
            // 如果从请求参数、路径参数、请求头部参数都无法获取到且非基本类型的话尝试从请求体中获取
            return null;
        }
    };

    ParameterValueResolver MULTIPART_FILE = (req, resp, methodDesc, methodParam, pathVariable) -> {
        Class<?> parameterType = methodParam.getType();
        if (parameterType.isArray()) {
            return req.getFiles();
        }
        return req.getFile();
    };
}
