package com.wemap.api.domain.user.entity;

import com.wemap.api.common.model.DateAudit;
import com.wemap.api.common.type.UserType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String loginId;
    private String password;
    private String name;
    private LocalDate birthday;
    private String email;
    @Enumerated(EnumType.STRING)
    private UserType userType;
    private Integer loginFailCount;
    private Boolean lock;

    @Builder
    public User(
            String loginId,
            String password,
            String name,
            LocalDate birthday,
            String email,
            UserType userType,
            Integer loginFailCount,
            Boolean lock
    ) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.userType = userType;
        this.loginFailCount = loginFailCount;
        this.lock = lock;
    }
}
