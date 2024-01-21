package com.wemap.api.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        //ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //커스텀 CORS 설정
                .cors(configurer -> configurer.disable())

                //CSRF protection 기능 사용하지 않음
                .csrf(configurer -> configurer.disable())

                //REST API -> HTTP Basic Auth 기반 로그인 인증 사용x
                .httpBasic(configurer -> configurer.disable())

                //REST API -> 일반적인 로그인 방식 사용x
                .formLogin(configurer -> configurer.disable())

                /*
                    STATELESS로 세선 정책을 사용한다는 것은
                    세션 쿠키 방식의 인증 메커니즘 방식을 사용하지 않겠다는 것을 의미.
                    인증에 성공한 이후라도 클라이언트가 다시 어떤 자원에 접근을 시도할 경우,
                    SecurityContextPersistenceFilter는 세션 존재 여부를 무시하고 항상 새로운 SecurityContext 객체를 생성하기 때문에
                    인증 성공 당시 SecurityContext에 저장됐던 Authentication 객체를 더 이상 참조할 수 없게 됨.
                 */
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/**/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/**/user/check/**").permitAll()
                            .requestMatchers("/api/**/user/signUp").permitAll()
                            .requestMatchers("/api/**/auth/**").permitAll()
                            .anyRequest().authenticated();
                })

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

                .headers(configurer -> configurer.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
