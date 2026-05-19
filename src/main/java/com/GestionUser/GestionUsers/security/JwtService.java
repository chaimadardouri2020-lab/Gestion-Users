package com.GestionUser.GestionUsers.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private long expiration;
    @Value("${jwt.refresh-expiration}") private long refreshExpiration;
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)); }
    public String generateToken(UserDetails u) { return buildToken(u, expiration); }
    public String generateRefreshToken(UserDetails u) { return buildToken(u, refreshExpiration); }
    private String buildToken(UserDetails u, long expiry) {
        return Jwts.builder().id(UUID.randomUUID().toString()).subject(u.getUsername())
            .claim("roles", u.getAuthorities()).issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiry)).signWith(getSigningKey()).compact();
    }
    public String extractUsername(String token) { return getClaims(token).getSubject(); }
    public boolean isTokenValid(String token, UserDetails u) {
        try { return extractUsername(token).equals(u.getUsername()) && !getClaims(token).getExpiration().before(new Date()); }
        catch (JwtException e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}