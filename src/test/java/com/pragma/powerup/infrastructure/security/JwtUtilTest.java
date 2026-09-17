package com.pragma.powerup.infrastructure.security;

import com.pragma.powerup.infrastructure.out.jpa.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "7f3a9c1e5b8d2f6a4c9e1b7d3f5a8c2e6b9d4f1a7c3e5b8d2f6a9c1e4b7d3f5a");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 864000L);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(2L);
        userEntity.setEmail("admin@admin.com");
        userEntity.setIdRole(1L);
        userDetails = new CustomUserDetails(userEntity);
    }

    @Test
    void generateToken_thenGenerateToken() {
        String token = jwtUtil.generateToken(userDetails);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_thenReturnEmail() {
        String token = jwtUtil.generateToken(userDetails);
        assertEquals("admin@admin.com", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_whenTokenCreated_thenTokenValid() {
        String token = jwtUtil.generateToken(userDetails);
        assertTrue(jwtUtil.isTokenValid(token, "admin@admin.com"));
    }

    @Test
    void isTokenValid_whenDifferentUsername_thenInvalid() {
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.isTokenValid(token, "usuario@usuario.com"));
    }

    @Test
    void isTokenValid_whenTokenExpired_thenInvalid() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -10L);
        String token = jwtUtil.generateToken(userDetails);
        assertFalse(jwtUtil.isTokenValid(token, "admin@admin.com"));
    }
}
