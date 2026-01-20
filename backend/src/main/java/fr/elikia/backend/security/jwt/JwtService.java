package fr.elikia.backend.security.jwt;

import fr.elikia.backend.bo.LogicResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

/**
 * Service responsible for JWT token creation, validation and extraction.

 * Uses HMAC SHA-based signing with a Base64-encoded secret key.
 */
@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Validation at Spring context startup
     * (fail-fast if bad configuration)
     */
    @PostConstruct
    void validate() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT secret missing or too short (min 32 chars)");
        }

        if (jwtExpirationMs == null || jwtExpirationMs <= 0) {
            throw new IllegalStateException("JWT expiration must be > 0");
        }
    }

    /**
     * Builds the secret signing key from a Base64-encoded string.
     *
     * @return cryptographic signing key
     */
    private Key getSecretKey() {
        // convert a string to base 64
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        // convert the base 64 to Key
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Generates a signed JWT token for an authenticated user
     *
     * @param email, role of the authenticated user
     * @return JWT token as String
     */
    public String generateToken(String email, String role) {
        // Token creation date
        Date issuedAt = new Date(System.currentTimeMillis());
        Date tokenLifeTime = new Date(System.currentTimeMillis() + jwtExpirationMs);  // Token lifetime
        return Jwts.builder()
                .subject(email) //subject data (identity)
                .issuedAt(issuedAt) // Token creation date
                .expiration(tokenLifeTime) // Token expiration date
                .claim("role", role)
                .signWith((getSecretKey())) // Crypt of the secret key
                .compact();

    }


    /**
     * Validates a JWT token.
     *
     * @param token JWT token
     * @return LogicResult indicating validity
     */
    public LogicResult<Boolean> verifyToken(String token){
        // if no token provided, return error
        if(token == null || token.isBlank()) {
            return new LogicResult<>("789", "Token vide", false);
        }
        try {
            // Check if the retrieved token is valid
            JwtParser jwtParser = Jwts.parser().verifyWith((SecretKey) getSecretKey()).build();
            jwtParser.parseSignedClaims(token).getPayload();
            // Otherwise
            return new LogicResult<>("204", "Tu peux passer", true);

        } catch (Exception e){
            return new LogicResult<>("789", "Token invalide", false);
        }

    }


    /**
     * Extracts user role from JWT token.
     *
     * @param token JWT token
     * @return role name
     */
    public String extractRole(String token) {
        JwtParser parser = Jwts.parser()
                .verifyWith((SecretKey) getSecretKey())
                .build();

        Claims claims = parser.parseSignedClaims(token).getPayload();
        return claims.get("role", String.class);
    }


}
