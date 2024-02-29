package my.practice.user.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Token 설정
 */
@RefreshScope
@Component
@Getter
public class TokenConfig {
    @Value("${token.secret}")
    private String secretKey;
    @Value("${token.header-name}")
    private String headerName;
    @Value("${token.refresh-header-name}")
    private String refreshHeaderName;
    @Value("${token.valid-time}")
    private long tokenValidTime;
    @Value("${token.refresh-valid-time}")
    private long refreshTokenValidTime;
}
