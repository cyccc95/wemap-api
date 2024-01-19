package com.wemap.api.common.response;

import com.wemap.api.common.code.StatusCode;
import lombok.Data;

@Data
public class ApiResponse {

    private Boolean success;
    private String code;
    private String message;
    private Object data;

    public ApiResponse(StatusCode statusCode) {
        this.success = statusCode.getHttpStatus().value() == 200 ||
                statusCode.getHttpStatus().value() == 201 ||
                statusCode.getHttpStatus().value() == 204;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    public ApiResponse(StatusCode statusCode, Object data) {
        this.success = statusCode.getHttpStatus().value() == 200 ||
                statusCode.getHttpStatus().value() == 201 ||
                statusCode.getHttpStatus().value() == 204;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }
}
