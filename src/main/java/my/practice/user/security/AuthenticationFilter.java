package my.practice.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.practice.user.dto.UserLoginDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
        super("/login", authenticationManager);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String username, password, rememberMe;
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            UserLoginDto dto = objectMapper.readValue(body, UserLoginDto.class);
            username = dto.getUsername();
            password = dto.getPassword();
            rememberMe = dto.getRememberMe();
        } catch (IOException e) {
            throw new RuntimeException("Body를 읽어오는 도중 에러가 발생했습니다.");
        }

        request.setAttribute("username", username);
        request.setAttribute("password", password);
        request.setAttribute("rememberMe", rememberMe);
        return null;
    }
}
