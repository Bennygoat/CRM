package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    @NotBlank(message = "驗證碼不可為空")
    private String token;

    @NotBlank(message = "新密碼不可為空")
    private String newPassword;

    @NotBlank(message = "確認密碼不可為空")
    private String confirmPassword;
}
