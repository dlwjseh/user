package my.practice.user.security;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import my.practice.user.vo.SecurityUser;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JwtTokenProvider {
    private final String secretKey;
    private static final long TOKEN_VALID_TIME = 1000L * 60L * 60L * 2L; // 2시간
    private final JsonMapper jsonMapper = JsonMapper.builder()
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false)
            .configure(MapperFeature.USE_ANNOTATIONS, false)
            .build();

    public JwtTokenProvider(ApplicationContext context) {
        String secret = context.getEnvironment().getProperty("secret");
        assert secret != null;
        this.secretKey = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(SecurityUser user, HttpServletRequest request) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("roles", user.getAuthorities());
        claims.put("auth",  new CryptoAES().encrypt(Compress.compressAndReturnB64(jsonMapper.writeValueAsString(user.getUserVo()))));
        claims.put("remoteAddress", getAddress(request));

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    public String getAddress(HttpServletRequest request) {
        final String UNKNOWN = "unknown";
        final List<String> HEADERS = List.of(
                "X-Forwarded-For"
                , "Proxy-Client-IP"
                , "WL-Proxy-Client-IP"
                ,"HTTP_CLIENT_IP"
                ,"HTTP_X_FORWARDED_FOR");

        return HEADERS.stream()
                .map(request::getHeader)
                .filter(StringUtils::hasText)
                .filter(ip -> !UNKNOWN.equalsIgnoreCase(ip))
                .findFirst()
                .orElse(request.getRemoteAddr())
    }

}
