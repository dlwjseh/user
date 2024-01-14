package my.practice.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import my.practice.user.service.CustomUserDetailService;
import my.practice.user.service.LocalDateTimeDeserializer;
import my.practice.user.service.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Spring Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailService customUserDetailService;

    @Value("${secret}")
    private String secretKey;

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
                .exceptionHandling((exception)
                        -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper())))
                // Http Request 인가 설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/test1/**").permitAll()
                        .anyRequest().authenticated()
                )
                .apply(new JwtSecurityConfig(secretKey, objectMapper()))
        ;

        return http.build();
    }

    /**
     * AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider customDaoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(passwordEncoder());
        authenticationProvider.setUserDetailsService(customUserDetailService);
        return authenticationProvider;
    }

    /**
     * 비밀번호 Encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

}
