package com.wemap.api.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class JwtUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void returnError(HttpServletResponse response, StatusCode statusCode) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setStatus(statusCode.getHttpStatus().value());
        objectMapper.writeValue(response.getWriter(), new ApiResponse(statusCode));
        response.getWriter().flush();
        response.getWriter().close();
    }

    public static String resolveAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!ValidationUtil.isNullOrBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
