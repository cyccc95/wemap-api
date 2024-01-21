package com.wemap.api.config.security;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.exception.CustomException;
import com.wemap.api.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String accessToken = JwtUtil.resolveAccessToken(request);
        if (!Objects.isNull(accessToken)) {
//            log.info("doFilterInternal: " + accessToken);
            try {
                if (jwtTokenProvider.validateAccessToken(accessToken)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    JwtUtil.returnError(response, StatusCode.ACCESS_TOKEN_INVALID);
                    log.error("CustomException:: code: {}, message: {}", StatusCode.ACCESS_TOKEN_INVALID.getCode(), StatusCode.ACCESS_TOKEN_INVALID.getMessage());
                    return;
                }
            } catch (CustomException ce) {
                JwtUtil.returnError(response, ce.getStatusCode());
                log.error("CustomException:: code: {}, message: {}", ce.getStatusCode().getCode(), ce.getStatusCode().getMessage());
                return;
            } catch (Exception e) {
                JwtUtil.returnError(response, StatusCode.INTERNAL_SERVER_ERROR);
                log.error("Exception:: code: {}, message: {}", StatusCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
