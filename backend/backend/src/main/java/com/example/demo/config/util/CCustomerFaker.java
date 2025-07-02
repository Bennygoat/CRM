package com.example.demo.config.util;

import com.example.demo.dto.request.CCustomerRegisterRequest;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

//import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.TimeUnit;



@Component
public class CCustomerFaker {

    // ✨ 1. 將 faker 設為實例變數，而非靜態變數，這更符合 Spring Bean 的生命週期管理
    private final Faker faker;

    // --- ✨ 2. 清理並統一化常數 ---
    private static final String[] LAST_NAMES = {"李", "郭", "陳", "林", "楊", "侯", "趙", "錢", "王", "張"};
    private static final String COMMON_CHINESE_CHARS = "宇傑宏偉俊彥哲凱軒誠翰霖宸睿晴萱穎雅婷";
    private static final String[] CITIES = {"台北市", "台中市", "台南市", "高雄市", "桃園市"};
    private static final String[] DISTRICTS = {"南屯區", "西區", "南區", "大雅區", "前鎮區", "平鎮區", "楊梅區", "永和區"};
    private static final String[] STREETS = {"忠孝路", "中正路", "大港路", "資展路", "大連路", "大進路", "公益路"};
    private static final String[] SECTIONS = {"一段", "二段", "三段"};
    private static final String[] EMAIL_DOMAINS = {"gmail.com", "me.com", "yahoo.com", "hotmail.com", "icloud.com"};
    private static final String[] TEL_AREA_CODES = {"02", "03", "04", "05", "07"};
    private static final String[] MOBILE_PREFIXES = {"0910", "0928", "0933", "0955", "0972", "0988"};

    public CCustomerFaker() {
        this.faker = new Faker(new Locale("zh-TW"));
    }

    public CCustomerRegisterRequest generateFakeCCustomerRequest() {
        CCustomerRegisterRequest request = new CCustomerRegisterRequest();

        String customerName = generateRandomCustomerName();
        // ✨ 3. 產生一次 email，並用於帳號和 email 欄位
        String email = generateRandomEmail();

        request.setAccount(email);
        request.setCustomerName(customerName);
        request.setEmail(email); // 確保 account 和 email 一致
        request.setPassword("Password123!");
        request.setBirthday(faker.date().birthday(18, 65).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        request.setCustomerTel(generateRandomPhone());
        request.setAddress(generateRandomAddress());

        return request;
    }

    public List<CCustomerRegisterRequest> generateFakeCCustomerRequests(int count) {
        List<CCustomerRegisterRequest> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requests.add(generateFakeCCustomerRequest());
        }
        return requests;
    }

    // --- Private Helper Methods ---

    private String generateRandomCustomerName() {
        String lastName = getRandomElement(LAST_NAMES);
        int firstNameLength = ThreadLocalRandom.current().nextInt(2, 4);
        StringBuilder firstName = new StringBuilder();
        for (int i = 0; i < firstNameLength; i++) {
            int index = ThreadLocalRandom.current().nextInt(COMMON_CHINESE_CHARS.length());
            firstName.append(COMMON_CHINESE_CHARS.charAt(index));
        }
        return lastName + firstName;
    }

    // ✨ 4. 修正 Email 產生邏輯
    private String generateRandomEmail() {
        // 使用 lorem().characters() 來產生一個不受語系影響的、固定長度的英數混合字串
        // 參數: (長度, 是否包含英文字母, 是否包含數字)
        String localPart = faker.lorem().characters(8, true, true).toLowerCase();
        String domainPart = getRandomElement(EMAIL_DOMAINS);
        return localPart + "@" + domainPart;
    }

    private String generateRandomPhone() {
        if (ThreadLocalRandom.current().nextDouble() < 0.8) {
            return getRandomElement(MOBILE_PREFIXES) + "-" + faker.number().digits(6);
        } else {
            return getRandomElement(TEL_AREA_CODES) + "-" + faker.number().digits(7);
        }
    }

    private String generateRandomAddress() {
        return getRandomElement(CITIES) +
                getRandomElement(DISTRICTS) +
                getRandomElement(STREETS) +
                getRandomElement(SECTIONS) +
                ThreadLocalRandom.current().nextInt(1, 446) + "號";
    }

    private <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
