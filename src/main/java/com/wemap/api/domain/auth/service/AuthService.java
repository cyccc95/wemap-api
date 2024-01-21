package com.wemap.api.domain.auth.service;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.exception.CustomException;
import com.wemap.api.common.util.EmailUtil;
import com.wemap.api.common.util.RedisUtil;
import com.wemap.api.common.util.ValidationUtil;
import com.wemap.api.config.security.JwtTokenProvider;
import com.wemap.api.config.security.UserDetailsImpl;
import com.wemap.api.domain.auth.dto.AuthDto;
import com.wemap.api.domain.user.entity.User;
import com.wemap.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailUtil emailUtil;
    private final JavaMailSender emailSender;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMilliSeconds;

    @Transactional
    public void sendCode(String email) {
        if (!ValidationUtil.checkEmail(email)) throw new CustomException(StatusCode.EMAIL_INVALID);
        String code = emailUtil.createCode();
        emailSender.send(emailUtil.createMessage(email, code));
        redisUtil.setValueWithTimeout("emailCode:" + email, code, 180000);
    }

    @Transactional
    public boolean checkCode(AuthDto.CheckCodeRequest checkCodeRequest) {
        String email = checkCodeRequest.getEmail();
        String code = checkCodeRequest.getCode();

        if (!ValidationUtil.checkEmail(email)) throw new CustomException(StatusCode.EMAIL_INVALID);
        if (!ValidationUtil.checkEmailCode(code)) throw new CustomException(StatusCode.EMAIL_CODE_INVALID);

        String codeInRedis = redisUtil.getValue("emailCode:" + email);
        if (Objects.isNull(codeInRedis)) throw new CustomException(StatusCode.EMAIL_CODE_EXPIRED);
        if (!codeInRedis.equals(code)) throw new CustomException(StatusCode.EMAIL_CODE_NOT_MATCH);
        redisUtil.deleteValue("emailCode:" + email);
        return true;
    }

    @Transactional(noRollbackFor = CustomException.class)
    public AuthDto.TokenResponse signIn(AuthDto.SignInRequest signInRequest) {
        String loginId = signInRequest.getLoginId();
        String password = signInRequest.getPassword();

        if (!ValidationUtil.checkLoginId(loginId)) throw new CustomException(StatusCode.LOGINID_INVALID);
        if (!ValidationUtil.checkPassword(password)) throw new CustomException(StatusCode.PASSWORD_INVALID);

        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new CustomException(StatusCode.USER_NOT_FOUND));

        if (user.getLock()) {
            throw new CustomException(StatusCode.USER_LOCK);
        } else if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            if (user.getLoginFailCount() < 4) {
                user.setLoginFailCount(user.getLoginFailCount() + 1);
            } else {
                user.setLoginFailCount(5);
                user.setLock(true);
            }
            throw new CustomException(StatusCode.PASSWORD_NOT_MATCH);
        } else {
            UserDetails userDetails = new UserDetailsImpl(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "", userDetails.getAuthorities());

            AuthDto.TokenResponse token = new AuthDto.TokenResponse();
            token.setAccessToken(jwtTokenProvider.createAccessToken(authentication));
            token.setRefreshToken(jwtTokenProvider.createRefreshToken());

            redisUtil.setValueWithTimeout("refreshToken:" + loginId, token.getRefreshToken(), refreshTokenValidityInMilliSeconds);

            user.setLoginFailCount(0);

            return token;
        }
    }

    @Transactional
    public AuthDto.TokenResponse reIssueToken(String accessToken, String refreshToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken.substring(7));
        String loginId = authentication.getPrincipal().toString();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new CustomException(StatusCode.REFRESH_TOKEN_INVALID);
        } else if (Objects.isNull(redisUtil.getValue("refreshToken:" + loginId))) {
            throw new CustomException(StatusCode.REFRESH_TOKEN_EXPIRED);
        } else {
            AuthDto.TokenResponse token = new AuthDto.TokenResponse();
            token.setAccessToken(jwtTokenProvider.createAccessToken(authentication));

            //refreshToken 만료시간이 1시간 이내로 남으면 refreshToken도 재발급
            if (redisUtil.getExpireToMilliseconds("refreshToken:" + loginId) < 3600000) {
                String newRefreshToken = jwtTokenProvider.createRefreshToken();
                token.setRefreshToken(newRefreshToken);
                redisUtil.setValueWithTimeout("refreshToken:" + loginId, newRefreshToken, refreshTokenValidityInMilliSeconds);
                return token;
            } else {
                token.setRefreshToken(null);
                return token;
            }
        }
    }

    @Transactional
    public void logout(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken.substring(7));
        String loginId = authentication.getPrincipal().toString();
        redisUtil.deleteValue("refreshToken:" + loginId);
        SecurityContextHolder.clearContext();
    }
}
