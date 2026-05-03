package com.finguard.expenses.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(JwtUtilContextTest.TestConfig.class)
@TestPropertySource(properties = {
        "app.jwt.secret=d50924495118031e95f1f60823f30f5c9c6bcd112eab6105c6641546d751d6f2",
        "app.jwt.expiration=3600000"
})
class JwtUtilContextTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void contextShouldCreateJwtUtilAndBindProperties() {
        String token = jwtUtil.generateToken("spring-user");

        assertNotNull(jwtUtil);
        assertNotNull(token);
        assertEquals("spring-user", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.isTokenValid(token));
    }

    @Configuration
    @Import(JwtUtil.class)
    static class TestConfig {
    }
}
