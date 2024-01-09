package my.practice.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtSecurityConfig implements SecurityConfigurer<DefaultSecurityFilterChain, HttpSecurity> {
    private final ObjectMapper objectMapper;
    public JwtSecurityConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void init(HttpSecurity builder) throws Exception {
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        builder.addFilterBefore(new AuthenticationFilter(authenticationManager, objectMapper), UsernamePasswordAuthenticationFilter.class);
    }
}
