package my.practice.user.security;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import my.practice.user.vo.SecurityUser;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class JwtTokenProvider {
    private final CryptoAES cryptoAES = new CryptoAES();
    private final String secretKey;
    public static final String HEADER_NAME = "X-AUTH-TOKEN";
    private static final long TOKEN_VALID_TIME = 1000L * 60L * 60L * 2L; // 2시간

    private final JsonMapper jsonMapper = JsonMapper.builder()
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, false)
            .configure(MapperFeature.USE_ANNOTATIONS, false)
            .build();

    public JwtTokenProvider(String secretKey) {
        assert secretKey != null;
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(SecurityUser user, HttpServletRequest request) throws IOException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        byte[] encodedAuth = Base64.getEncoder().encode(compressString(jsonMapper.writeValueAsString(user.getUserVo())));
        claims.put("auth", cryptoAES.encrypt(encodedAuth));
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

    private String getAddress(HttpServletRequest request) {
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
                .orElse(request.getRemoteAddr());
    }

    public byte[] compressString(String input) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(input.getBytes());
        }
        return os.toByteArray();
    }

    public byte[] decompressBytes(byte[] compressedBytes) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(compressedBytes);
        }
        return os.toByteArray();
    }

}
