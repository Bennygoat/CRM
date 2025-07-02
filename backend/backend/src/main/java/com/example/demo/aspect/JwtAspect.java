package com.example.demo.aspect;

import com.example.demo.security.JwtTool;
import com.example.demo.security.JwtUserPayload;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(1)
@Component
public class JwtAspect {

    @Around("@annotation(com.example.demo.security.CheckJwt)")
    public Object checkJwt(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("JwtAspect checkJwt START");
        HttpServletRequest req =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Token 格式錯誤");
        }
        String token = authHeader.substring(7);

        // 1. 解析 Token，取得完整的 Payload 物件
        JwtUserPayload payload = JwtTool.parseToken(token);
        if (payload == null) {
            throw new JwtException("Token 過期或無效");
        }

        // ✨ 新邏輯：為了讓新功能(如購物車)運作，存入整個 payload 物件
        req.setAttribute("userPayload", payload);
        System.out.println("Set request attribute 'userPayload' = " + payload);

        // ✨ 保留舊邏輯：為了讓舊功能繼續運作，單獨存入 account 和 role
        req.setAttribute("account", payload.getAccount());
        System.out.println("Set request attribute 'account' = " + payload.getAccount());

        req.setAttribute("role", payload.getRole());
        System.out.println("Set request attribute 'role' = " + payload.getRole());

        System.out.println("Token ok, account = " + payload.getAccount());
        System.out.println("JwtAspect checkJwt END");
        return joinPoint.proceed();
    }
}