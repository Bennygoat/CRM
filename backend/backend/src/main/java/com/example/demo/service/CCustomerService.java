package com.example.demo.service;

import com.example.demo.dto.request.UpdateCCustomerProfileRequest;
import com.example.demo.dto.response.CCustomerProfileResponse;
import com.example.demo.dto.response.CustomerCouponDto;
import com.example.demo.entity.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.exception.AccountAlreadyExistsException;
import com.example.demo.exception.EmailAlreadyExistsException;
import com.example.demo.exception.ForgetAccountOrPasswordException;
import com.example.demo.exception.UsernameNotFoundException;
import com.example.demo.repository.CCustomerRepo;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PasswordResetTokenRepo;
import com.example.demo.repository.VIPLevelRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;


@Service
public class CCustomerService {
    private final CCustomerRepo cCustomerRepo;
    private final BCryptPasswordEncoder encoder;
    private final OrderRepository orderRepository;
    private final VIPLevelRepo vipLevelRepo;
    private final VIPLevelService vipLevelService;
    private final PasswordResetTokenRepo tokenRepo;
    private final CouponTemplateService couponTemplateService;
    private final CustomerCouponService customerCouponService;

    // 註冊禮優惠券的代碼
    private static final String NEW_MEMBER_COUPON_CODE = "NEW_MEMBER_90";

    // 建構自注入customerRepo、encoder
    public CCustomerService(CCustomerRepo cCustomerRepo,
                            OrderRepository orderRepository,
                            VIPLevelRepo vipLevelRepo,
                            VIPLevelService vipLevelService,
                            PasswordResetTokenRepo tokenRepo,
                            CouponTemplateService couponTemplateService,
                            CustomerCouponService customerCouponService) {
        this.cCustomerRepo = cCustomerRepo;
        this.vipLevelRepo = vipLevelRepo;
        this.vipLevelService = vipLevelService;
        this.tokenRepo = tokenRepo;
        this.orderRepository = orderRepository;
        this.couponTemplateService = couponTemplateService;
        this.customerCouponService = customerCouponService;
        this.encoder = new BCryptPasswordEncoder(); // 或改為在外部注入

    }

    // 檢視帳號是否已存在
    public Boolean checkAccountExist(String account){
        return cCustomerRepo.findByAccount(account).isPresent();
    }

    // 檢視email是否已被註冊
    public Boolean checkEmailExist(String email){
        return cCustomerRepo.existsByEmail(email);
    }

    // 密碼強度規範
    private void validatePasswordStrength(String password) {
        if (password.length() < 12) {
            throw new IllegalArgumentException("密碼長度需至少12碼");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("密碼需包含至少一個大寫英文字母");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("密碼需包含至少一個小寫英文字母");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("密碼需包含至少一個數字");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new IllegalArgumentException("密碼需包含至少一個特殊符號");
        }
    }

    // 註冊 + 加密
    public CCustomer register(String account
                            , String customerName
                            , String password
                            , String email
                            , String address
                            , LocalDate birthday){
        if(checkAccountExist(account)){
            throw new AccountAlreadyExistsException(account);
        }

        if(checkEmailExist(email)){
            throw new EmailAlreadyExistsException(email);
        }

        validatePasswordStrength(password);

        // ✨ 新增 #1: 產生一個唯一的客戶編號
        // 這裡使用 "C-" 前綴加上一個隨機的8位碼
        String newCustomerCode = "C-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        CCustomer newCCustomer = CCustomer.builder()
                .account(account)
                .customerName(customerName)
                .password(encoder.encode(password))
                .email(email)
                .address(address)
                .birthday(birthday)
                .customerCode(newCustomerCode) // ✨ 新增 #2: 在建立物件時設定 customerCode
                .isActive(true)
                .isDeleted(false)
                .build();

        // --- 您原有的儲存用戶邏輯，完全保留 ---
        CCustomer savedCustomer = cCustomerRepo.save(newCCustomer);

        // ==========================================================
        // 【新功能】在此處新增發送優惠券的邏輯
        // ==========================================================
        try {
//            log.info("嘗試為新用戶(ID:{})發送註冊禮優惠券...", savedCustomer.getCustomerId());
            CouponTemplate welcomeCouponTemplate = couponTemplateService.findTemplateByCode(NEW_MEMBER_COUPON_CODE);

            if (welcomeCouponTemplate.getIssuedQuantity() < welcomeCouponTemplate.getTotalQuantity()) {
                customerCouponService.grantCouponToCustomer(savedCustomer, welcomeCouponTemplate);
//                log.info("成功發送註冊禮優惠券(Code:{})給用戶(ID:{})。", NEW_MEMBER_COUPON_CODE, savedCustomer.getCustomerId());
            } else {
//                log.warn("註冊禮優惠券(Code:{})庫存不足，無法發送給新用戶(ID:{})。", NEW_MEMBER_COUPON_CODE, savedCustomer.getCustomerId());
            }
        } catch (Exception e) {
            // 如果發券失敗，只記錄錯誤，不影響用戶註冊成功。
//            log.error("為用戶(ID:{})發送註冊禮優惠券時發生預期外的錯誤。", savedCustomer.getCustomerId(), e);
        }
        // ==========================================================
        // 【新功能結束】
        // ==========================================================

        return savedCustomer; // 返回儲存後的用戶實體
    }




    // 登入驗證 (JWT + OUATH2)+ 拋給別人我已經登入的資訊供後續開發
    public CCustomer login(String account, String password){
        // 測帳號 //暫時，之後要改成能有重設帳密功能，跳轉介面?發送EMAIL?顯示錯誤訊息?
        CCustomer loginCCustomer = cCustomerRepo.findByAccount(account)
                .orElseThrow(() -> new ForgetAccountOrPasswordException(account, password));

        // 測密碼 //暫時，之後要改成能有重設帳密功能，跳轉介面?發送EMAIL?顯示錯誤訊息?
        String dbPassword = loginCCustomer.getPassword();
        if(!encoder.matches(password, dbPassword)){
            throw new ForgetAccountOrPasswordException(account, password);
        }

        return loginCCustomer;
    }

    // 檢視顧客資料: 顯示用戶個人基本資訊（帳號、姓名、電話、地址等）資料查詢、權限驗證（只能看自己的資料）
    public CCustomerProfileResponse getProfile(String account) {
        CCustomer customer = cCustomerRepo.findByAccount(account)
                .orElseThrow(()-> new UsernameNotFoundException("找不到使用者: " + account));

        // 【重要修改】將 Set<CustomerCoupon> 轉換為 List<CustomerCouponDto>
        List<CustomerCouponDto> couponDtos = customer.getCustomerCoupons().stream()
                .map(customerCoupon -> CustomerCouponDto.builder()
                        .customerCouponId(customerCoupon.getId())
                        .name(customerCoupon.getCouponTemplate().getName())
                        .description(customerCoupon.getCouponTemplate().getDescription())
                        .couponType(customerCoupon.getCouponTemplate().getCouponType())
                        .discountValue(customerCoupon.getCouponTemplate().getDiscountValue())
                        .minPurchaseAmount(customerCoupon.getCouponTemplate().getMinPurchaseAmount())
                        .validTo(customerCoupon.getCouponTemplate().getValidTo())
                        .status(customerCoupon.getStatus())
                        .build())
                .collect(Collectors.toList());


        return new CCustomerProfileResponse(
                customer.getAccount(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getBirthday(),
                couponDtos,
                customer.getSpending(),
                customer.getVipLevel()
        );
    }


    // todo: 0630，忘記密碼要接到gmail認證信，通過了才可以進行更改密碼?
    // 忘記密碼，密碼驗證
    // 忘記密碼 - 產生重設 token 並寄信（這裡先回傳 token，未整合 Gmail）
    public String generateResetToken(String email) {
        CCustomer customer = cCustomerRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("查無Email: " + email));

        // 清除舊的 token（可選）
        List<PasswordResetToken> oldTokens = tokenRepo.findByEmailAndUsedFalse(email);
        oldTokens.forEach(t -> t.setUsed(true));
        tokenRepo.saveAll(oldTokens);

        // 產生新 token 並儲存
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setCreatedAt(LocalDateTime.now());
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30)); // 30 分鐘有效
        resetToken.setUsed(false);

        tokenRepo.save(resetToken);

        // 實際應該寄 email，此處先回傳 token
        return token;
    }

    // 使用 token 重設密碼
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("無效或過期的 token"));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token 已過期");
        }

        CCustomer customer = cCustomerRepo.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("找不到對應顧客"));

        validatePasswordStrength(newPassword);
        customer.setPassword(encoder.encode(newPassword));

        cCustomerRepo.save(customer);
        // 標記 token 為已使用
        resetToken.setUsed(true);
        tokenRepo.save(resetToken);
    }


    // 更改基本資料: 讓用戶更新個人資訊（含忘記密碼、密碼更改）資料驗證、密碼加密更新、資料一致性

    public CCustomerProfileResponse updateProfile(String account, UpdateCCustomerProfileRequest request) {
        CCustomer customer = cCustomerRepo.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("帳號不存在"));

        // 更新基本資料，並防止為空值
        if (request.getCustomerName() != null) {
            customer.setCustomerName(request.getCustomerName());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        // 如果 newPassword 不是空的，執行密碼修改流程
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            // 驗證舊密碼正確
            if (!encoder.matches(request.getOldPassword(), customer.getPassword())) {
                throw new IllegalArgumentException("舊密碼錯誤");
            }

            // 驗證新密碼強度
            validatePasswordStrength(request.getNewPassword());

            // 加密新密碼
            customer.setPassword(encoder.encode(request.getNewPassword()));
        }

        CCustomer savedCustomer = cCustomerRepo.save(customer);

        // 【重要修改】將 Set<CustomerCoupon> 轉換為 List<CustomerCouponDto>
        List<CustomerCouponDto> couponDtos = savedCustomer.getCustomerCoupons().stream()
                .map(customerCoupon -> CustomerCouponDto.builder()
                        .customerCouponId(customerCoupon.getId())
                        .name(customerCoupon.getCouponTemplate().getName())
                        .description(customerCoupon.getCouponTemplate().getDescription())
                        .couponType(customerCoupon.getCouponTemplate().getCouponType())
                        .discountValue(customerCoupon.getCouponTemplate().getDiscountValue())
                        .minPurchaseAmount(customerCoupon.getCouponTemplate().getMinPurchaseAmount())
                        .validTo(customerCoupon.getCouponTemplate().getValidTo())
                        .status(customerCoupon.getStatus())
                        .build())
                .collect(Collectors.toList());


        return new CCustomerProfileResponse(
                savedCustomer.getAccount(),
                savedCustomer.getCustomerName(),
                savedCustomer.getEmail(),
                savedCustomer.getAddress(),
                savedCustomer.getBirthday(),
                couponDtos,
                savedCustomer.getSpending(),
                savedCustomer.getVipLevel()
        );
    }

    // ID找帳號
    public Long getCustomerIdByAccount(String account) {
        return cCustomerRepo.findByAccount(account)
                .map(CCustomer::getCustomerId)
                .orElseThrow(() -> new UsernameNotFoundException("找不到顧客: " + account));
    }

    // 帳號是否啟用中
    public boolean isCustomerActive(String account) {
        return cCustomerRepo.findByAccount(account)
                .map(CustomerBase::isAvailable)
                .orElse(false); // 找不到代表不合法，當作不允許操作
    }

    // 帳號刪除(真刪除)
    public void deleteAccountPermanently(String account) {
        CCustomer customer = cCustomerRepo.findByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException("找不到帳號: " + account));

        cCustomerRepo.delete(customer);
    }

    // ✨✨✨ 新增這個完整的方法 ✨✨✨
    /**
     * 更新客戶的總消費金額。
     * 此方法會查找客戶所有狀態為 COMPLETE 的訂單，並加總其金額。
     * @param customerId 要更新的客戶ID
     */
    @Transactional
    public void updateCustomerSpending(Long customerId) {
        // 1. 先找到對應的客戶實體，若不存在則立即失敗 (採用我的版本優點)
        CCustomer customer = cCustomerRepo.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("更新消費金額時找不到客戶 ID: " + customerId));

        // 2. 根據 customerId 查找其所有歷史訂單
        List<Order> orders = orderRepository.findByCCustomer_CustomerIdOrderByOrderdateDesc(customerId);

        // 3. 過濾出狀態為 COMPLETE 的訂單，並安全地加總其 totalAmount (採用您的版本優點)
        double totalSpending = orders.stream()
                .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETE)
                .mapToDouble(order -> order.getTotalAmount() != null ? order.getTotalAmount() : 0.0) // 處理 null
                .sum();

        // 4. 更新客戶的 spending 欄位並儲存
        customer.setSpending((long) totalSpending); // 將 double 轉為 Long
        cCustomerRepo.save(customer);

        // 5. 更新viplevel
        evaluateAndUpdateVipLevel(customerId);
    }


    // 總消費10000vip免運費, 50000vvip終身9折(可結合其他優惠使用)，一段時間沒消費降級(90days)
    @Transactional
    public void evaluateAndUpdateVipLevel(Long customerId) {
        CCustomer customer = cCustomerRepo.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("找不到客戶 ID: " + customerId));

        Long spending = customer.getSpending();
        List<VIPLevel> levels = vipLevelRepo.findAllByOrderByUpgradeThresholdAsc();

        // 找到最適等級（spending >= 升級門檻）
        VIPLevel matchedLevel = null;
        for (VIPLevel level : levels) {
            if (spending >= level.getUpgradeThreshold()) {
                matchedLevel = level;
            }
        }
        // 額外檢查是否該降級（以 90 天沒消費為例）
        Optional<Order> lastOrderOpt = orderRepository.findByCCustomer_CustomerIdOrderByOrderdateDesc(customerId)
                .stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETE)
                .findFirst();

        if (lastOrderOpt.isPresent()) {
            LocalDate lastOrderDate = lastOrderOpt.get().getOrderdate();
            long daysSinceLastOrder = ChronoUnit.DAYS.between(lastOrderDate, LocalDate.now());

            // 超過90天未消費，且花費不到當前等級的降級門檻
            if (daysSinceLastOrder > 90) {
                VIPLevel currentLevel = customer.getVipLevel();
                if (currentLevel != null && spending < currentLevel.getDowngradeThreshold()) {
                    Optional<VIPLevel> downgradeLevel = vipLevelService.downgradeOneLevel(currentLevel);
                    downgradeLevel.ifPresent(customer::setVipLevel);
                    cCustomerRepo.save(customer);
                    return;
                }
            }
        }

        // 正常升級流程（如果比當前高）
        if (matchedLevel != null && !matchedLevel.equals(customer.getVipLevel())) {
            customer.setVipLevel(matchedLevel);
            cCustomerRepo.save(customer);
        }
    }

    // log, 開api給系統管理者: 註冊時紀錄註冊時間、修改實紀錄修改時間、下單時紀錄下單時間
}
