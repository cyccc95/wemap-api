package com.wemap.api.domain.user.service;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.exception.CustomException;
import com.wemap.api.common.type.UserType;
import com.wemap.api.common.util.FormatUtil;
import com.wemap.api.common.util.ValidationUtil;
import com.wemap.api.domain.user.dto.UserDto;
import com.wemap.api.domain.user.entity.User;
import com.wemap.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public String checkLoginId(String loginId) {
        if (!ValidationUtil.checkLoginId(loginId)) throw new CustomException(StatusCode.LOGINID_INVALID);
        if (userRepository.existsByLoginId(loginId)) throw new CustomException(StatusCode.LOGINID_DUPLICATED);
        return loginId;
    }

    public String checkPassword(String password) {
        if (!ValidationUtil.checkPassword(password)) throw new CustomException(StatusCode.PASSWORD_INVALID);
        return password;
    }

    public String checkName(String name) {
        if (!ValidationUtil.checkName(name)) throw new CustomException(StatusCode.NAME_INVALID);
        return name;
    }

    public String checkBirthday(String birthday) {
        if (!ValidationUtil.checkBirthday(birthday)) throw new CustomException(StatusCode.BIRTHDAY_INVALID);
        return birthday;
    }

    @Transactional(readOnly = true)
    public String checkEmail(String email) {
        if (!ValidationUtil.checkEmail(email)) throw new CustomException(StatusCode.EMAIL_INVALID);
        if (userRepository.existsByEmail(email)) throw new CustomException(StatusCode.EMAIL_DUPLICATED);
        return email;
    }

    @Transactional
    public Long signUp(UserDto.SignUpRequest signUpInfo) {
        String loginId = checkLoginId(signUpInfo.getLoginId());
        String password = checkPassword(signUpInfo.getPassword());
        String name = checkName(signUpInfo.getName());
        String birthday = checkBirthday(signUpInfo.getBirthday());
        String email = checkEmail(signUpInfo.getEmail());

        User user = User.builder()
                .loginId(loginId)
                .password(bCryptPasswordEncoder.encode(password))
                .name(name)
                .birthday(FormatUtil.formatBirthDayStringToLocalDate(birthday))
                .email(email)
                .userType(UserType.USER)
                .loginFailCount(0)
                .lock(false)
                .build();
        return userRepository.save(user).getIdx();
    }

    @Transactional(readOnly = true)
    public UserDto.SimpleResponse getSimpleUserInfo(Long userIdx) {
        User user = userRepository.findByIdx(userIdx).orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));
        return user.toSimpleUserDto();
    }
}
