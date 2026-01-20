package fr.elikia.backend.security;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {
    private final JwtService jwtService = new JwtService();

    @Test
    void itShouldGenerateAndValidateToken() {

        // Generate a valid token for an ADMIN role
        String token = jwtService.generateToken("admin@mail.com", "ADMIN");

        // Verify it returns valid
        LogicResult<Boolean> result = jwtService.verifyToken(token);

        assertEquals("204", result.getCode());
        assertTrue(result.getData());
    }

    @Test
    void itShouldFailWithInvalidToken() {
        LogicResult<Boolean> result = jwtService.verifyToken("invalid.token");
        assertEquals("789", result.getCode());
        assertFalse(result.getData());
    }

    @Test
    void itShouldExtractRoleFromToken() {
        String token = jwtService.generateToken("admin@mail.com", "ADMIN");

        String role = jwtService.extractRole(token);

        assertEquals("ADMIN", role);
    }
}
