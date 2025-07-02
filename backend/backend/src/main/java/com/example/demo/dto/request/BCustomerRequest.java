package com.example.demo.dto.request;

import com.example.demo.enums.BCustomerIndustry;
import com.example.demo.enums.BCustomerLevel;
import com.example.demo.enums.BCustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BCustomerRequest {

    /**
     * 用於接收「建立」或「更新」客戶時的請求資料。
     * 只包含允許客戶端修改的欄位。
     */

    @NotBlank(message = "客戶名稱不能為空")
    private String customerName;

    private BCustomerIndustry industry;
    private BCustomerType BCustomerType;
    private BCustomerLevel BCustomerLevel;

    private String customerAddress;
    private String customerTel;
    private String tinNumber; //TODO(josh)

    @Email(message = "客戶電子郵件格式不正確")
    private String customerEmail;

    private List<Long> tagIds;

}
