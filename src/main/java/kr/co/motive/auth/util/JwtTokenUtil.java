package kr.co.motive.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.co.motive.auth.dto.UserResponseDto;
import kr.co.motive.common.code.UserErrorCode;
import kr.co.motive.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp-ms}")
    private long accessExpMs;

    @Value("${jwt.refresh-exp-ms}")
    private long refreshExpMs;

    private StringRedisTemplate redisTemplate;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String role) {
        return createToken(userId, role, accessExpMs);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, null, refreshExpMs);
    }

    private String createToken(Long userId, String role, long ttlMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);

        JwtBuilder builder = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256);

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    /**
     * @param token access token 또는 refresh token — 서명/만료만 검증하고 userId를 꺼냄
     */
    public Long getUserId(String token){

        try{
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.parseLong(claims.getSubject());
        }catch(JwtException | IllegalArgumentException e){
            throw new CustomException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

    }
}
