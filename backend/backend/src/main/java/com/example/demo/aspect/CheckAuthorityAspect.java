package com.example.demo.aspect;

import com.example.demo.entity.User;
import com.example.demo.exception.NoAuthorityException;
import com.example.demo.exception.UsernameNotFoundException;
import com.example.demo.repository.UserRepo;
import com.example.demo.security.CheckAuthority;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.file.AccessDeniedException;

@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
public class CheckAuthorityAspect {

    private final UserRepo userRepo;

    @Around("@annotation(checkAuthority)")
    public Object checkUserAuthority(ProceedingJoinPoint joinPoint, CheckAuthority checkAuthority) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String account = (String) req.getAttribute("account");

        if (account == null) {
            throw new NoAuthorityException("找不到帳號，請先通過JWT驗證");
        }

        User user = userRepo.findByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + account));

        if (!user.isActive() || user.isDeleted()) {
            throw new NoAuthorityException("帳號已被停用或刪除");
        }

        String requiredCode = checkAuthority.value().getCode();
        boolean hasAuthority = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getCode().equals(requiredCode));

        if (!hasAuthority) {
            throw new NoAuthorityException("您沒有存取此資源的權限: " + requiredCode);
        }

        return joinPoint.proceed(); // ✅ 注意不能漏這個
    }
}
