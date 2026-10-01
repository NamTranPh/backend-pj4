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

    /** Kênh phát token — quyết định token dùng được ở khu nào, KHÔNG phải role. */
    public static final String CHANNEL_ADMIN = "admin";
    public static final String CHANNEL_USER = "user";

    /** Claim phân biệt access token với refresh token. */
    public static final String CLAIM_TOKEN_TYPE = "typ";
    public static final String CLAIM_CHANNEL = "ch";
    public static final String TOKEN_TYPE_ACCESS = "access";

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * Nhận thẳng email thay vì UserDetails: trước đây caller phải gọi
     * {@code loadUserByUsername()} (một truy vấn DB) chỉ để lấy lại đúng cái email nó đã có.
     *
     * @param channel CHANNEL_ADMIN nếu token phát từ cửa CMS, CHANNEL_USER nếu phát từ cửa người dùng.
     */
    public String generateAccessToken(String subject, String channel) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_CHANNEL, channel);
        return createToken(claims, subject, jwtProperties.getExpiration());
    }

    public String generateRefreshToken(String subject, String tokenId, String channel) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put(CLAIM_CHANNEL, channel);
        if (tokenId != null && !tokenId.isBlank()) {
            claims.put("tokenId", tokenId);
        }
        return createToken(claims, subject, jwtProperties.getRefreshExpiration());
    }

    public static String channelOf(boolean admin) {
        return admin ? CHANNEL_ADMIN : CHANNEL_USER;
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

    public String getSubjectFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /** Claim "type" của refresh token. */
    public String getTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    /** Claim "typ" của access token. */
    public String getAccessTokenTypeFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get(CLAIM_TOKEN_TYPE, String.class));
    }

    public String getChannelFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get(CLAIM_CHANNEL, String.class));
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
        final String username = getSubjectFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
