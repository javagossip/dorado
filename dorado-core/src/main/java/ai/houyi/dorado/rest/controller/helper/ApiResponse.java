package ai.houyi.dorado.rest.controller.helper;


public class ApiResponse {

    private static final int SUCCESS_CODE = 200;

    //具体由业务来定义code, 如：200, 400, 500
    private int code = SUCCESS_CODE;
    private String message;
    private Object data;

    //json序列化需要无参构造函数
    public ApiResponse() {
    }

    //后台调用出现异常的情况下，不需要data的响应构造函数
    public ApiResponse(int code, String message) {
        this(code, message, null);
    }

    public ApiResponse(Object data) {
        this(SUCCESS_CODE, null, data);
    }

    public ApiResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(data);
    }

    public static ApiResponse fail(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
