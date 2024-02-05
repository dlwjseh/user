package my.practice.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import my.practice.user.exception.InvalidTokenException;
import my.practice.user.vo.SecurityUser;
import my.practice.user.vo.UserVo;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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
import java.util.HashSet;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class JwtTokenProvider {
    private final CryptoAES cryptoAES = new CryptoAES();
    private final String secretKey;
    public static final String HEADER_NAME = "X-AUTH-TOKEN";
    public static final String REFRESH_HEADER_NAME = "X-AUTH-REFRESH-TOKEN";
    private static final long TOKEN_VALID_TIME = 1000L * 60L * 5L; // 5분
    private static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60L * 60L * 2L; // 2시간

    private final ObjectMapper objectMapper;

    public JwtTokenProvider(String secretKey, ObjectMapper objectMapper) {
        assert secretKey != null;
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        this.objectMapper = objectMapper;
    }

    /**
     * 토큰 발급
     */
    public void issueToken(HttpServletResponse response, SecurityUser user) throws IOException {
        try {
            response.setHeader(JwtTokenProvider.HEADER_NAME, this.createToken(user));
            response.setHeader(JwtTokenProvider.REFRESH_HEADER_NAME, this.createRefreshToken(user.getUsername()));
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        // Response 생성
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("result", "OK")));
        response.getWriter().flush();
    }

    /**
     * Token 생성
     */
    public String createToken(SecurityUser user) throws IOException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        byte[] encodedAuth = Base64.getEncoder().encode(compressString(objectMapper.writeValueAsString(user.getUserVo())));
        claims.put("auth", cryptoAES.encrypt(encodedAuth));
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
    }

    /**
     * 유효한 토큰인지 확인
     */
    public void validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                throw new InvalidTokenException("만료된 토큰입니다.");
            }
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new InvalidTokenException("잘못된 토큰입니다.");
        }
    }

    /**
     * Token으로부터 Authentication 조회
     */
    public Authentication getAuthentication(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            String encodedAuth = (String) claims.getBody().get("auth");
            String auth = new String(decompressBytes(new CryptoAES().decrypt(encodedAuth.getBytes(StandardCharsets.UTF_8))));
            UserVo userVo = objectMapper.readValue(auth, UserVo.class);
            return new UsernamePasswordAuthenticationToken(new SecurityUser(userVo.getEmail(), "", userVo),
                    "", new HashSet<>());
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidTokenException("잘못된 토큰입니다.");
        }
    }

    /**
     * RefreshToken을 통해 유저명 조회
     */
    public String getUsernameFromRefreshToken(String refreshToken) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken).getBody().getSubject();
    }

    /**
     * String 압축
     */
    public byte[] compressString(String input) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(os)) {
            dos.write(input.getBytes());
        }
        return os.toByteArray();
    }

    /**
     * String 압축 해제
     */
    public byte[] decompressBytes(byte[] compressedBytes) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (OutputStream ios = new InflaterOutputStream(os)) {
            ios.write(compressedBytes);
        }
        return os.toByteArray();
    }

}
