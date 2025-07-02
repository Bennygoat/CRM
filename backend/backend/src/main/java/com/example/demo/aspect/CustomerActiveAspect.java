package com.example.demo.aspect;

import com.example.demo.service.CCustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.file.AccessDeniedException;

@Aspect
@Order(2)
@Component
public class CustomerActiveAspect {

    private final CCustomerService customerService;

    public CustomerActiveAspect(CCustomerService customerService) {
        this.customerService = customerService;
    }

    @Around("@annotation(com.example.demo.security.CheckCustomerActive)")
    public Object checkCustomerActive(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String account = (String) req.getAttribute("account");

        if (account == null) {
            throw new AccessDeniedException("請先登入");
        }

        boolean isActive = customerService.isCustomerActive(account);
        if (!isActive) {
            throw new AccessDeniedException("帳號已停用，無法操作");
        }

        return joinPoint.proceed();
    }
}
