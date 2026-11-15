package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * 簽發與解析 JWT。
 * 金鑰由設定 jwt.secret 提供（application.properties 對應到環境變數 JWT_SECRET），
 * 在 Spring 啟動建立這個 Bean 時就檢查，缺少或格式不對會讓啟動直接失敗。
 */
@Component
public class JwtTool {

    /** HS256 規格（RFC 7518 3.2）要求金鑰至少 256 bit。 */
    static final int MIN_KEY_BYTES = 32;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;

    private static final String HOW_TO_GENERATE =
            "請產生 32 bytes 的隨機值並轉成 Base64，設定到環境變數 JWT_SECRET，步驟見 README。";

    private final Key key;

    public JwtTool(@Value("${jwt.secret}") String base64Secret) {
        this.key = toKey(base64Secret);
    }

    private static Key toKey(String base64Secret) {
        if (base64Secret == null || base64Secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET 是空的。" + HOW_TO_GENERATE);
        }
        byte[] bytes;
        try {
            bytes = Decoders.BASE64.decode(base64Secret.trim());
        } catch (DecodingException e) {
            throw new IllegalStateException("JWT_SECRET 不是合法的 Base64。" + HOW_TO_GENERATE, e);
        }
        if (bytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("JWT_SECRET 解碼後只有 " + bytes.length
                    + " bytes，HS256 至少要 " + MIN_KEY_BYTES + " bytes。" + HOW_TO_GENERATE);
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(JwtUserPayload payload) {
        return Jwts.builder()
                .setSubject(payload.getAccount())
                .claim("id", payload.getId())
                .claim("name", payload.getName())
                .claim("role", payload.getRole())
                .claim("authorities", payload.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 Token，轉成 JwtUserPayload。
     * 簽章不符（例如用舊金鑰簽的）、格式錯誤或過期都回傳 null。
     */
    public JwtUserPayload parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new JwtUserPayload(
                    claims.get("id", Long.class),
                    claims.getSubject(),
                    claims.get("name", String.class),
                    claims.get("role", String.class),
                    claims.get("authorities", List.class)
            );
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("JWT 解析失敗: " + e.getMessage());
            return null;
        }
    }

    public String parseTokenToAccount(String token) {
        JwtUserPayload payload = parseToken(token);
        return payload != null ? payload.getAccount() : null;
    }
}
