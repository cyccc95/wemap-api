package com.wemap.api.common.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusCode {

    /** CustomErrorCode
     * 1~2자리
     * 00 unassigned
     * 01 user
     * 02 loginId
     * 03 password
     * 04 name
     * 05 birthday
     * 06 email
     * 07 emailCode
     * 08 accessToken
     * 09 refreshToken
     *
     * 3~4자리
     * 00 unassigned
     * 01 invalid
     * 02 duplicated
     * 03 not match
     * 04 not found
     * 05 expired
     * 06 malformed
     * 07 unsupported
     * 08 forbidden
    */

    OK(HttpStatus.OK, "S200", "OK"),
    CREATED(HttpStatus.CREATED, "S201", "Created"),
    NO_CONTENT(HttpStatus.NO_CONTENT, "S204", "No Content"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "E400", "Bad Request"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "E401", "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "E403", "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "Not Found"),
    CONFLICT(HttpStatus.CONFLICT, "E409", "Conflict"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "Internal Server Error"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "E503", "Service Unavailable"),

    //토큰
    ACCESS_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "E0801", "AccessToken Invalid"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "E0805", "AccessToken Expired"),
    ACCESS_TOKEN_MALFORMED(HttpStatus.BAD_REQUEST, "E0806", "AccessToken Malformed"),
    ACCESS_TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "E0807", "AccessToken Unsupported"),
    REFRESH_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "E0901", "RefreshToken Invalid"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "E0905", "RefreshToken Expired"),
    REFRESH_TOKEN_MALFORMED(HttpStatus.BAD_REQUEST, "E0906", "RefreshToken Malformed"),
    REFRESH_TOKEN_UNSUPPORTED(HttpStatus.BAD_REQUEST, "E0907", "RefreshToken Unsupported"),

    //회원가입, 로그인
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E0104", "User Not Found"),
    USER_LOCK(HttpStatus.FORBIDDEN, "E0108", "User Lock"),
    LOGINID_INVALID(HttpStatus.BAD_REQUEST, "E0201", "LoginId Invalid"),
    LOGINID_DUPLICATED(HttpStatus.CONFLICT, "E0202", "LoginId Duplicated"),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "E0301", "Password Invalid"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "E0303", "Password Not Match"),
    NAME_INVALID(HttpStatus.BAD_REQUEST, "E0401", "Name Invalid"),
    BIRTHDAY_INVALID(HttpStatus.BAD_REQUEST, "E0501", "Birthday Invalid"),
    EMAIL_INVALID(HttpStatus.BAD_REQUEST, "E0601", "Email Invalid"),
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "E0602", "Email Duplicated"),
    EMAIL_CODE_INVALID(HttpStatus.BAD_REQUEST, "E0701", "EmailCode Invalid"),
    EMAIL_CODE_NOT_MATCH(HttpStatus.BAD_REQUEST, "E0703", "EmailCode Not Match"),
    EMAIL_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "E0705", "EmailCode Expired");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
