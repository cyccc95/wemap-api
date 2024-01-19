package com.wemap.api.common.exception;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.response.ApiResponse;
import com.wemap.api.common.response.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<ApiResponse> handleCustomException(CustomException ce) {
        StatusCode statusCode = ce.getStatusCode();
        log.error("CustomException:: code: {}, message: {}", statusCode.getCode(), statusCode.getMessage());
        return ResponseMessage.error(statusCode);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ApiResponse> handleException(Exception e) {
        StatusCode statusCode = StatusCode.INTERNAL_SERVER_ERROR;
        log.error("Exception:: code: {}, message: {}", statusCode.getCode(), e.getMessage());
        return ResponseMessage.error(statusCode);
    }
}
