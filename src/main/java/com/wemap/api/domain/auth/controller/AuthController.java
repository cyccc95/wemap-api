package com.wemap.api.domain.auth.controller;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.response.ApiResponse;
import com.wemap.api.common.response.ResponseMessage;
import com.wemap.api.domain.auth.dto.AuthDto;
import com.wemap.api.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long COOKIE_EXPIRATION_IN_MILLISECONDS;

    @GetMapping("/emailCode")
    public ResponseEntity<ApiResponse> sendCode(@RequestParam String email) {
        authService.sendCode(email);
        return ResponseMessage.success(StatusCode.OK);
    }

    @PostMapping("/emailCode")
    public ResponseEntity<ApiResponse> checkCode(@RequestBody AuthDto.CheckCodeRequest checkCodeRequest) {
        return ResponseMessage.success(StatusCode.OK, authService.checkCode(checkCodeRequest));
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse> signIn(@RequestBody AuthDto.SignInRequest signInRequest) {
        AuthDto.TokenResponse token = authService.signIn(signInRequest);
        HttpCookie httpCookie = ResponseCookie
                .from("refreshToken", token.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION_IN_MILLISECONDS / 1000)
                .httpOnly(true)
                .secure(true)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, httpCookie.toString());
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());

        return ResponseMessage.success(StatusCode.OK, httpHeaders);
    }

    @PostMapping("/reIssue")
    public ResponseEntity<ApiResponse> reIssueToken(
            @RequestBody AuthDto.ReIssueRequest reIssueRequest,
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        AuthDto.TokenResponse token = authService.reIssueToken(reIssueRequest.getAccessToken(), refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token.getAccessToken());

        if (!Objects.isNull(token.getRefreshToken())) {
            HttpCookie httpCookie = ResponseCookie
                    .from("refreshToken", token.getRefreshToken())
                    .maxAge(COOKIE_EXPIRATION_IN_MILLISECONDS / 1000)
                    .httpOnly(true)
                    .secure(true)
                    .build();
            httpHeaders.add(HttpHeaders.SET_COOKIE, httpCookie.toString());
        }

        return ResponseMessage.success(StatusCode.OK, httpHeaders);
    }

    @PostMapping("logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody AuthDto.LogoutRequest logoutRequest) {
        authService.logout(logoutRequest.getAccessToken());
        HttpCookie httpCookie = ResponseCookie
                .from("refreshToken", "")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, httpCookie.toString());

        return ResponseMessage.success(StatusCode.NO_CONTENT, httpHeaders);
    }
}
