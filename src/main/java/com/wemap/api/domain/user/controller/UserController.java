package com.wemap.api.domain.user.controller;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.response.ApiResponse;
import com.wemap.api.common.response.ResponseMessage;
import com.wemap.api.domain.user.dto.UserDto;
import com.wemap.api.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/check/loginId")
    public ResponseEntity<ApiResponse> checkLoginId(@RequestParam String loginId) {
        return ResponseMessage.success(StatusCode.OK, userService.checkLoginId(loginId));
    }

    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam String email) {
        return ResponseMessage.success(StatusCode.OK, userService.checkEmail(email));
    }

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse> signUp(@RequestBody UserDto.SignUpRequest signUpInfo) {
        Long userIdx = userService.signUp(signUpInfo);
        URI location = URI.create("/api/v1/user/" + userIdx);
        return ResponseMessage.success(StatusCode.CREATED, location);
    }

    @GetMapping("/{userIdx}")
    public ResponseEntity<ApiResponse> getSimpleUserInfo(@PathVariable Long userIdx) {
        return ResponseMessage.success(StatusCode.OK, userService.getSimpleUserInfo(userIdx));
    }
}
