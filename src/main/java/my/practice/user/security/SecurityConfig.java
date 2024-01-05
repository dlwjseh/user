package my.practice.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;

    /**
     * Http Security 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // csrf(Cross Site Request Forgery) RestApi는 Stateless하기 때문에 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // form login 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // Http basic Auth 기반 인증 비활성화
                .httpBasic(HttpBasicConfigurer::disable)
                // Session 생성 규칙 > 사용하지 않음
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증 실패 시 핸들링
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(authenticationEntryPoint()))
                // Http Request 인가 설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/test1/**").permitAll()
                        .anyRequest().authenticated()
                )
        ;

        return http.build();
    }

    /**
     * 비밀번호 Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 실패 처리
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

}
