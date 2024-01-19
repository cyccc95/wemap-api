package com.wemap.api.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {

    USER("USER", "사용자"),
    ADMIN("ADMIN", "관리자");

    private final String role;
    private final String description;
}
