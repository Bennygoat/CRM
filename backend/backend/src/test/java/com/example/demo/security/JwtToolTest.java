package com.example.demo.security;

import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtToolTest {

    /** 每次測試都產生新的隨機金鑰，repo 裡不放任何金鑰字串。 */
    public static String randomBase64Key(int bytes) {
        byte[] raw = new byte[bytes];
        new SecureRandom().nextBytes(raw);
        return Encoders.BASE64.encode(raw);
    }

    private static JwtUserPayload admin() {
        return new JwtUserPayload(1L, "admin", "超級管理員", "USER", List.of("USER_MANAGE"));
    }

    @Test
    void 同一把金鑰簽發的token可以解析() {
        JwtTool tool = new JwtTool(randomBase64Key(32));

        JwtUserPayload parsed = tool.parseToken(tool.createToken(admin()));

        assertNotNull(parsed);
        assertEquals("admin", parsed.getAccount());
        assertEquals(1L, parsed.getId());
    }

    @Test
    void 換金鑰後舊token被拒絕() {
        JwtTool oldServer = new JwtTool(randomBase64Key(32));
        JwtTool newServer = new JwtTool(randomBase64Key(32));
        String oldToken = oldServer.createToken(admin());

        assertNull(newServer.parseToken(oldToken));
    }

    @Test
    void 金鑰是空的就無法建立() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new JwtTool(" "));
        assertTrue(e.getMessage().contains("JWT_SECRET"));
    }

    @Test
    void 金鑰不足32bytes就無法建立() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new JwtTool(randomBase64Key(31)));
        assertTrue(e.getMessage().contains("至少要 32 bytes"));
    }

    @Test
    void 金鑰不是Base64就無法建立() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new JwtTool("這不是 base64!!"));
        assertTrue(e.getMessage().contains("Base64"));
    }
}
