package com.wemap.api.domain.auth.dto;

import lombok.Data;

public class AuthDto {

    @Data
    public static class CheckCodeRequest {
        private String email;
        private String code;
    }

    @Data
    public static class SignInRequest {
        private String loginId;
        private String password;
    }

    @Data
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    public static class ReIssueRequest {
        private String accessToken;
    }

    @Data
    public static class LogoutRequest {
        private String accessToken;
    }
}
