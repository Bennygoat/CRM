package com.example.demo.security;

import com.example.demo.entity.VIPLevel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.List;


public class JwtTool {
    private static final String SECRET = "ben-ben-is-such-a-nice-guy-so-heis-giving-u-a-key-x0x0";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public static String createToken(
            // 先註解，原本寫死，改成彈性的jwtpayload
            // String customerName, String account, Long customerId, VIPLevel vipLevel
            JwtUserPayload payload){
        return Jwts.builder()
                .setSubject(payload.getAccount())
                .claim("id", payload.getId())
                .claim("name", payload.getName())
                .claim("role", payload.getRole())
                .claim("authorities", payload.getAuthorities()) // Optional
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
            // return Jwts.builder()
            // .setSubject(account)
            // .claim("customerId", customerId)
            // .claim("customerName", customerName)
            // .claim("viplevel", vipLevel)
            // .setIssuedAt(new Date())
            // .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            // .signWith(key, SignatureAlgorithm.HS256)
            // .compact();

    }

    /**
     * ✨【修改版】✨
     * 解析 Token，並將其內容直接轉換成一個結構化的 JwtUserPayload 物件。
     * 這是推薦的做法，因為它提供了型別安全和更好的可讀性。
     * @param token JWT 字串
     * @return 包含使用者資訊的 JwtUserPayload 物件，若解析失敗或過期則回傳 null。
     */
    public static JwtUserPayload parseToken(String token) {
        try {
            // 1. 取得金鑰並解析出通用的 Claims 物件 (和舊版一樣)
            Key skey = Keys.hmacShaKeyFor(SECRET.getBytes());
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(skey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 2. ✨ 將鬆散的 Claims 資料，轉換並封裝到我們定義的 JwtUserPayload 物件中
            return new JwtUserPayload(
                    claims.get("id", Long.class),          // 從 claims 取出 id
                    claims.getSubject(),                       // Subject 就是 account
                    claims.get("name", String.class),      // 從 claims 取出 name
                    claims.get("role", String.class),      // 從 claims 取出 role
                    claims.get("authorities", List.class)  // 從 claims 取出 authorities
            );

        } catch (JwtException e) {
            // 如果 Token 無效、格式錯誤或過期，印出錯誤訊息並回傳 null
            System.err.println("JWT 解析失敗: " + e.getMessage());
            return null;
        }
    }

    /**
     * ✨【更新版】✨
     * 一個方便的輔助方法，用於直接從 Token 中取得帳號字串。
     * 它現在會呼叫上面新的 parseToken 方法來運作。
     * @param token JWT 字串
     * @return 帳號字串，若解析失敗則回傳 null。
     */
    public static String parseTokenToAccount(String token) {
        // 1. 呼叫新的 parseToken 方法，取得錢包 (JwtUserPayload)
        JwtUserPayload payload = parseToken(token);

        // 2. 從錢包中取出帳號，如果錢包存在的話
        return payload != null ? payload.getAccount() : null;
    }
}
