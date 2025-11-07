package com.softsuave.crud.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory Comment: This class is responsible for handling all JWT-related tasks:
 * 1. Generate a token after successful login.
 * 2. Extract username or role from an existing token.
 * 3. Validate tokens (check if expired or tampered).
 *
 * It is annotated with @Component so that Spring can manage it as a bean.
 * This allows other classes (like filters and controllers) to inject and use it easily.
 */
@Component
public class JWTutil {

    // -------------- Configuration Properties --------------

    // The token’s validity duration (in milliseconds),
    // fetched automatically from application.properties
    @Value("${jwt.expiration.ms}")
    private long EXPIRATION_TIME;

    // The secret key used for signing and verifying JWTs.
    // It must be kept secure and private.
    @Value("${jwt.secret}")
    private String SECRET;

    // Used internally to store the cryptographic version of our secret.
    private Key key;

    // -------------- Initialization --------------

    /**
     * After the SECRET is injected by Spring,
     * @PostConstruct ensures this method runs automatically
     * to convert the plain text secret into a secure Key object.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // -------------- Token Generation --------------

    /**
     * Generates a new JWT when a user logs in.
     *
     * @param username - the username of the authenticated user
     * @param role - the user's role (stored as a custom claim)
     * @return a compact JWT string
     */
    public String generateToken(String username, String role) {
        // Claims are extra data stored inside the token (like role, email, etc.)
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        // Build the token
        return Jwts.builder()
                .setClaims(claims)                          // Add our custom data
                .setSubject(username)                       // The token "subject" → who it belongs to
                .setIssuedAt(new Date())                    // When the token was created
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expiry time
                .signWith(key, SignatureAlgorithm.HS256)    // Sign the token using our secret key
                .compact();                                 // Convert it to a compact String form
    }

    // -------------- Token Extraction --------------

    /**
     * Extracts the username from a token.
     * Used by JwtAuthFilter during authentication checks.
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // -------------- Token Validation --------------

    /**
     * Validates the token by checking:
     * 1. If the username matches the user from DB.
     * 2. If the token is still valid (not expired).
     */
    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    /**
     * Helper method to check if the token has expired.
     */
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // -------------- Internal Helper Methods --------------

    /**
     * Parses the token using our signing key to verify its integrity
     * and returns all the claims (payload data).
     *
     * If the token is expired or tampered with, this method throws an exception.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)              // The same key used for signing
                .build()
                .parseClaimsJws(token)           // Validates and parses the token
                .getBody();                      // Returns the token’s data part
    }

}
