package com.example.demo.controller;

import com.example.demo.dto.request.UpdateCCustomerProfileRequest;
import com.example.demo.dto.request.UpdateUserProfileRequest;
import com.example.demo.dto.request.UserQueryRequest;
import com.example.demo.dto.request.UserRegisterRequest;
import com.example.demo.dto.response.CCustomerProfileResponse;
import com.example.demo.dto.response.UserProfileResponse;
import com.example.demo.entity.Authority;
import com.example.demo.entity.User;
import com.example.demo.enums.AuthorityCode;
import com.example.demo.security.CheckAuthority;
import com.example.demo.security.CheckJwt;
import com.example.demo.service.CCustomerService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @CheckJwt
    @PostMapping("/register")
    public ResponseEntity<User> register(HttpServletRequest request,
                                         @RequestBody UserRegisterRequest req) throws AccessDeniedException {
//        List<Authority> authorities = req.getAuthorityCodes().stream()
//                .map(code -> authorityRepo.findByCode(code)
//                        .orElseThrow(() -> new IllegalArgumentException("找不到權限代碼: " + code)))
//                .collect(Collectors.toList());

        String operatorAccount = (String) request.getAttribute("account");

        User createdUser = userService.register(
                operatorAccount,
                req.getAccount(),
                req.getUserName(),
                req.getPassword(),
                req.getEmail(),
                req.getAccessEndDate(),
                req.getAuthorityCodes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 查看自己資料
    @GetMapping("/profile")
    @CheckJwt
    public ResponseEntity<UserProfileResponse> getProfile(HttpServletRequest request) {
        String account = (String) request.getAttribute("account");
        return ResponseEntity.ok(userService.getUserProfileByAccount(account));
    }

    // 更改自己資料
    @CheckJwt
    @PutMapping("/profile/update")
    public ResponseEntity<UserProfileResponse> updateProfile(
            HttpServletRequest request,
            @RequestBody UpdateUserProfileRequest updateRequest) {

        String account = (String) request.getAttribute("account");

        UserProfileResponse updatedProfile = userService.updateOwnProfile(account, updateRequest);

        return ResponseEntity.ok(updatedProfile);
    }

    // 忘記密碼
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        String token = userService.generateResetToken(email);
        return ResponseEntity.ok("Token: " + token); // 可改成寄信或顯示前端畫面
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("密碼已成功重設");
    }

    //  系統管理員查閱單一使用者資料（by account）
    @CheckJwt
    @CheckAuthority(AuthorityCode.USER_READ)
    @GetMapping("/{account}")
    public ResponseEntity<UserProfileResponse> getUserByAccount(@PathVariable String account) {
        UserProfileResponse profile = userService.getUserProfileByAccount(account);
        return ResponseEntity.ok(profile);
    }

    // 系統管理員查閱多筆使用者資料
    @CheckJwt
    @CheckAuthority(AuthorityCode.USER_READ)
    @PostMapping("/search")
    public ResponseEntity<Page<UserProfileResponse>> searchUsers(@RequestBody UserQueryRequest req) {
        return ResponseEntity.ok(userService.queryUsers(req));
    }

    //  系統管理員編輯使用者資料（含權限、激活時間、密碼）
    @CheckJwt
    @CheckAuthority(AuthorityCode.USER_UPDATE)
    @PutMapping("/{account}")
    public ResponseEntity<UserProfileResponse> updateUserByAdmin(
            @PathVariable String account,
            @RequestBody UpdateUserProfileRequest request
    ) {
        UserProfileResponse updated = userService.updateProfileByAdmin(account, request);
        return ResponseEntity.ok(updated);
    }

    // 系統管理員軟刪除使用者帳號（停權處理）
    @CheckJwt
    @CheckAuthority(AuthorityCode.USER_DELETE)
    @DeleteMapping("/{account}")
    public ResponseEntity<String> disableUser(@PathVariable String account) {
        userService.disableUserAccountByAdmin(account);
        return ResponseEntity.ok("使用者已停權");
    }

    // 系統管理員軟刪除客戶帳號（管理員操作）
    @CheckJwt
    @CheckAuthority(AuthorityCode.CUSTOMER_DELETE)
    @DeleteMapping("/customer/{account}")
    public ResponseEntity<String> disableCustomer(@PathVariable String account) {
        userService.disableCustomerAccount(account);
        return ResponseEntity.ok("顧客帳號已停權");
    }

}
