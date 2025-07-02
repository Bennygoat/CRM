package com.example.demo.controller;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.CCustomerLoginResponse;
import com.example.demo.entity.CCustomer;
import com.example.demo.security.CheckJwt;
import com.example.demo.security.JwtTool;
import com.example.demo.security.JwtUserPayload;
import com.example.demo.service.CCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


///login, /test, /get-token
/// 登入、JWT、驗證類
@RestController
@RequestMapping("/api/customer/auth")
public class CCustomerAuthController {

    private final CCustomerService cCustomerService;
    public CCustomerAuthController(CCustomerService cCustomerService) {
        this.cCustomerService = cCustomerService;
    }

    @Operation(summary = "客戶登入並獲取 JWT Token")
    @PostMapping("/login")
    public ResponseEntity<CCustomerLoginResponse> login(@RequestBody LoginRequest req){
        CCustomer cCustomer = cCustomerService.login(
                req.getAccount(),
                req.getPassword()
        );

        String token =  JwtTool.createToken(JwtUserPayload.fromCustomer(cCustomer));

        CCustomerLoginResponse res = CCustomerLoginResponse.builder()
                .token(token)
                .account(cCustomer.getAccount())
                .customerName(cCustomer.getCustomerName()) //TODO(joshkuei): Changed from customerName() to name().
                .email(cCustomer.getEmail())
                .birthday(cCustomer.getBirthday())
                .createdAt(cCustomer.getCreatedAt())
                .spending(cCustomer.getSpending())
                .build();
        return ResponseEntity.ok(res);
    }

    @Operation(summary = "測試 JWT 驗證")
    @GetMapping("/test")
    @CheckJwt
    public String testEndpoint() {
        return "Token 驗證成功，你進來了！";
    }

    // /forget-password


}
