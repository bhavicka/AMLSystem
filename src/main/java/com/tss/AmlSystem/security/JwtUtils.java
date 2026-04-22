package com.tss.AmlSystem.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(String email, String bankName, String schemaName, List<String> roles) {
        return Jwts.builder()
                .subject(email)
                .claim("bankName", bankName)
                .claim("schemaName", schemaName)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getSchemaFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("schemaName", String.class);
    }

    public boolean validateJwtToken(String authToken) {
//        try {
//
//        } catch (Exception ignore){}
//        return false;
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
        return true;
    }
    public List<String> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // The same key used for signing
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Pull the "roles" claim we saw earlier
        return claims.get("roles", List.class);
    }
}
