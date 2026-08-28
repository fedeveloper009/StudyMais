package com.projeto.studymais.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMillis
    ) {
            if (secret == null || secret.isBlank() || secret.length() < 32) {
                throw new IllegalArgumentException(
                        "JWT_SECRET must be configured with at least 32 characters."
                );
            }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    /**
     * Gera um token assinado contendo o email do usuario como subject.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    public String generateToken(String username) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(expirationMillis)))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extrai o email do usuario de um token assinado e nao expirado.
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Confirma assinatura, usuario associado e validade temporal do token.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        return claims.getSubject() != null
                && claims.getSubject().equals(userDetails.getUsername())
                && expiration != null
                && expiration.after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
