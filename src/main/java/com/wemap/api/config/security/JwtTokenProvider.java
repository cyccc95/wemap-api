package com.wemap.api.config.security;

import com.wemap.api.common.code.StatusCode;
import com.wemap.api.common.exception.CustomException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

    private static Key signingKey;
    private static String LOGIN_ID = "loginId";
    private static String AUTHORITIES_KEY = "role";
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-milliseconds}")
    private long accessTokenValidityInMilliSeconds;
    @Value("${jwt.refresh-token-validity-in-milliseconds}")
    private long refreshTokenValidityInMilliSeconds;

    //시크릿키 설정
    @Override
    public void afterPropertiesSet() {
        try {
            byte[] secretKeyBytes = Decoders.BASE64.decode(secretKey);
            signingKey = Keys.hmacShaKeyFor(secretKeyBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //AccessToken 생성
    public String createAccessToken(Authentication authentication) {
        String loginId = authentication.getPrincipal().toString();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        Long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + accessTokenValidityInMilliSeconds))
                .setSubject("accessToken")
                .claim(LOGIN_ID, loginId)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
        return accessToken;
    }

    //RefreshToken 생성
    public String createRefreshToken() {
        Long now = System.currentTimeMillis();
        String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS512")
                .setExpiration(new Date(now + refreshTokenValidityInMilliSeconds))
                .setSubject("refreshToken")
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
        return refreshToken;
    }

    //토큰에서 claims 추출
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //토큰에서 authentication 추출
    public Authentication getAuthentication(String accessToken) {
        Claims claims = getClaims(accessToken);
        String loginId = claims.get(LOGIN_ID).toString();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(loginId, "", authorities);
    }

    //AccessToken 검증
    public boolean validateAccessToken(String accessToken) {
//        log.info("validateAccessToken: " + accessToken);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(StatusCode.ACCESS_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(StatusCode.ACCESS_TOKEN_MALFORMED);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(StatusCode.ACCESS_TOKEN_UNSUPPORTED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //RefreshToken 검증
    public boolean validateRefreshToken(String refreshToken) {
//        log.info("validateRefreshToken: " + refreshToken);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(refreshToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(StatusCode.REFRESH_TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(StatusCode.REFRESH_TOKEN_MALFORMED);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(StatusCode.REFRESH_TOKEN_UNSUPPORTED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
