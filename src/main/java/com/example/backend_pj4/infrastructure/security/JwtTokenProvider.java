package com.example.backend_pj4.infrastructure.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.backend_pj4.infrastructure.config.properties.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;
    
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtProperties.getExpiration());
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(userDetails, null);
    }

    public String generateRefreshToken(UserDetails userDetails, String tokenId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        if (tokenId != null && !tokenId.isBlank()) {
            claims.put("tokenId", tokenId);
        }
        return createToken(claims, userDetails.getUsername(), jwtProperties.getRefreshExpiration());
    }
    
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        var builder = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate);

        if (jwtProperties.getIssuer() != null && !jwtProperties.getIssuer().isBlank()) {
            builder.setIssuer(jwtProperties.getIssuer());
        }

        return builder.signWith(getSigningKey()).compact();
    }
    
    public String getPhoneFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    public String getTokenIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("tokenId", String.class));
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getPhoneFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
