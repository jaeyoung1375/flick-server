package kr.co.motive.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.motive.common.code.UserErrorCode;
import kr.co.motive.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private static final String SECRET = "test-secret-key-for-jwt-unit-test-must-be-32bytes+";

    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "accessExpMs", 1000L * 60 * 30);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshExpMs", 1000L * 60 * 60 * 24 * 14);
    }

    @Test
    @DisplayName("정상 토큰이면 userId(subject)를 그대로 반환한다")
    void getUserId_returnsUserId_whenTokenValid() {
        String token = jwtTokenUtil.createRefreshToken(1L);

        Long userId = jwtTokenUtil.getUserId(token);

        assertEquals(1L, userId);
    }

    @Test
    @DisplayName("서명이 다른 토큰이면 INVALID_REFRESH_TOKEN 예외를 던진다")
    void getUserId_throws_whenSignatureInvalid() {
        Key otherKey = Keys.hmacShaKeyFor(
                "other-secret-key-different-from-original-32b".getBytes(StandardCharsets.UTF_8));
        String forgedToken = Jwts.builder()
                .setSubject("1")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(otherKey, SignatureAlgorithm.HS256)
                .compact();

        CustomException ex = assertThrows(CustomException.class,
                () -> jwtTokenUtil.getUserId(forgedToken));

        assertEquals(UserErrorCode.INVALID_REFRESH_TOKEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("만료된 토큰이면 INVALID_REFRESH_TOKEN 예외를 던진다")
    void getUserId_throws_whenTokenExpired() {
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshExpMs", -1000L); // 발급 즉시 만료되도록
        String expiredToken = jwtTokenUtil.createRefreshToken(1L);

        CustomException ex = assertThrows(CustomException.class,
                () -> jwtTokenUtil.getUserId(expiredToken));

        assertEquals(UserErrorCode.INVALID_REFRESH_TOKEN, ex.getResponseCode());
    }

    @Test
    @DisplayName("형식이 깨진 토큰 문자열이면 INVALID_REFRESH_TOKEN 예외를 던진다")
    void getUserId_throws_whenTokenMalformed() {
        CustomException ex = assertThrows(CustomException.class,
                () -> jwtTokenUtil.getUserId("not-a-valid-jwt"));

        assertEquals(UserErrorCode.INVALID_REFRESH_TOKEN, ex.getResponseCode());
    }
}