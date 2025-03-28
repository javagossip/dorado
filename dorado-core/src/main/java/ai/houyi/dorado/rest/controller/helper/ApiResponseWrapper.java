package ai.houyi.dorado.rest.controller.helper;

import java.io.InputStream;

import ai.houyi.dorado.rest.http.MethodReturnValueHandler;
import ai.houyi.dorado.rest.util.MethodDescriptor;
import ai.houyi.dorado.rest.util.TypeUtils;

public class ApiResponseWrapper implements MethodReturnValueHandler {

    @Override
    public Object handleMethodReturnValue(Object value, MethodDescriptor methodDescriptor) {
        return ApiResponse.ok(value);
    }

    @Override
    public boolean supportsReturnType(MethodDescriptor methodDescriptor) {
        Class<?> returnType = methodDescriptor.getReturnType();
        return returnType != byte[].class && returnType != InputStream.class && returnType != ApiResponse.class &&
                !TypeUtils.isProtobufMessage(returnType);
    }
}
