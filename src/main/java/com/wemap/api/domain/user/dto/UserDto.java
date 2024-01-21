package com.wemap.api.domain.user.dto;

import com.wemap.api.common.type.UserType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {

    @Data
    public static class SignUpRequest {
        private String loginId;
        private String password;
        private String name;
        private String birthday;
        private String email;
    }

    @Data
    public static class SimpleResponse {
        private Long idx;
        private String loginId;
        private String name;
        private LocalDate birthday;
        private String email;
    }

    @Data
    public static class DetailResponse {
        private Long idx;
        private String loginId;
        private String name;
        private LocalDate birthday;
        private String email;
        private UserType userType;
        private Boolean lock;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }
}
