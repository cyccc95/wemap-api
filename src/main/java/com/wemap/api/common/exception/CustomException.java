package com.wemap.api.common.exception;

import com.wemap.api.common.code.StatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final StatusCode statusCode;

    public CustomException(StatusCode statusCode) {
        super();
        this.statusCode = statusCode;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
