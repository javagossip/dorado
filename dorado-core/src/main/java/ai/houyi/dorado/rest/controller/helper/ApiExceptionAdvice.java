package ai.houyi.dorado.rest.controller.helper;

import ai.houyi.dorado.rest.annotation.ExceptionAdvice;
import ai.houyi.dorado.rest.annotation.ExceptionType;

@ExceptionAdvice
public class ApiExceptionAdvice {

    @ExceptionType(Exception.class)
    public ApiResponse handleException(Exception ex) {
        return ApiResponse.fail(500, ex.getMessage());
    }
}
