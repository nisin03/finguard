package com.finguard.expenses.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private static final String SECRET = "d50924495118031e95f1f60823f30f5c9c6bcd112eab6105c6641546d751d6f2";
    private static final String USERNAME = "alice";

    @Test
    void generateToken_shouldReturnTokenString() {
        JwtUtil jwtUtil = createJwtUtil(3_600_000L);

        String token = jwtUtil.generateToken(USERNAME);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_shouldReturnTokenSubject() {
        JwtUtil jwtUtil = createJwtUtil(3_600_000L);
        String token = jwtUtil.generateToken(USERNAME);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(USERNAME, extractedUsername);
    }

    @Test
    void isTokenValid_shouldReturnTrueForFreshToken() {
        JwtUtil jwtUtil = createJwtUtil(3_600_000L);
        String token = jwtUtil.generateToken(USERNAME);

        boolean valid = jwtUtil.isTokenValid(token);

        assertTrue(valid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForTamperedToken() {
        JwtUtil jwtUtil = createJwtUtil(3_600_000L);
        String token = jwtUtil.generateToken(USERNAME);

        boolean valid = jwtUtil.isTokenValid(tamperToken(token));

        assertFalse(valid);
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtUtil jwtUtil = createJwtUtil(50L);
        String token = jwtUtil.generateToken(USERNAME);

        Thread.sleep(100L);

        boolean valid = jwtUtil.isTokenValid(token);

        assertFalse(valid);
    }

    private JwtUtil createJwtUtil(long expiration) {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        jwtUtil.init();
        return jwtUtil;
    }

    private String tamperToken(String token) {
        String[] segments = token.split("\\.");
        String signature = segments[2];
        int tamperIndex = Math.max(0, signature.length() / 2);
        char original = signature.charAt(tamperIndex);
        char replacement = original == 'a' ? 'b' : 'a';
        String tamperedSignature = signature.substring(0, tamperIndex)
                + replacement
                + signature.substring(tamperIndex + 1);
        return segments[0] + "." + segments[1] + "." + tamperedSignature;
    }
}
