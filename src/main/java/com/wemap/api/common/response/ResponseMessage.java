package com.wemap.api.common.response;

import com.wemap.api.common.code.StatusCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@Slf4j
@NoArgsConstructor
public class ResponseMessage {

    public static ResponseEntity<ApiResponse> success(StatusCode statusCode) {
        return ResponseEntity.status(statusCode.getHttpStatus()).body(new ApiResponse(statusCode));
    }

    public static ResponseEntity<ApiResponse> success(StatusCode statusCode, Object data) {
        return ResponseEntity.status(statusCode.getHttpStatus()).body(new ApiResponse(statusCode, data));
    }

    public static ResponseEntity<ApiResponse> success(StatusCode statusCode, URI location) {
        return ResponseEntity.status(statusCode.getHttpStatus()).location(location).body(new ApiResponse(statusCode));
    }

    public static ResponseEntity<ApiResponse> success(StatusCode statusCode, HttpHeaders httpHeaders) {
        return ResponseEntity.status(statusCode.getHttpStatus()).headers(httpHeaders).body(new ApiResponse(statusCode));
    }

    public static ResponseEntity<ApiResponse> error(StatusCode statusCode) {
        return ResponseEntity.status(statusCode.getHttpStatus()).body(new ApiResponse(statusCode));
    }
}
