package com.example.demo.util;

import com.example.demo.dto.request.ContactRequest;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Component
public class ContactFaker {

    private static final Faker faker = new Faker(new Locale("zh-TW"));

    // --- ✨ 1. 定義假資料的常數 ---
    private static final String[] LAST_NAMES = {"李", "郭", "陳", "林", "楊", "侯", "趙", "錢"};
    private static final String COMMON_CHINESE_CHARS = "宇傑宏偉俊彥哲凱軒誠翰霖宸睿"; // 用於產生名字的隨機中文字

    private static final String[] TITLES = {"總經理", "總監", "經理", "處長", "組長", "課長", "專員", "特助", "助理", "老闆", "老闆娘", "小老闆"};

    private static final String[] PHONE_PREFIXES = {"02", "03", "04", "05", "06", "07", "08", "09"};

    private static final String[] NOTES = {
            "對A產品線特別感興趣", "需要下週再跟進", "決策者，需要特別關注",
            "已寄送報價單", "要求提供詳細規格書", "預計下個月有採購需求",
            "是我們VIP客戶的推薦", "喜歡打高爾夫球"
    };
    private static final List<String> DOMAINS = List.of(
            "yahoo.com", "me.com", "icloud.com", "ai.com", "hotmail.com", "gmail.com"
    );


    private static final Random random = new Random();

    private static String generateCustomEmail() {
        // 取英文名 + 數字，確保是英文開頭
        String firstName = faker.name().firstName().replaceAll("[^a-zA-Z]", "");
        String lastName = faker.name().lastName().replaceAll("[^a-zA-Z]", "");
        int number = random.nextInt(1000);

        String username = (firstName + lastName + number).toLowerCase();
        String domain = DOMAINS.get(random.nextInt(DOMAINS.size()));
        return username + "@" + domain;
    }





    /**
     * 產生一個隨機的 ContactRequest 物件。
     * @param customerIds 可選的客戶ID列表，用於關聯聯絡人。
     * @return 包含假聯絡人資料的 ContactRequest
     */
    public ContactRequest generateFakeContactRequest(List<Long> customerIds) {
        Long customerId = null;
        if (customerIds != null && !customerIds.isEmpty()) {
            // 使用 ThreadLocalRandom，更高效
            customerId = customerIds.get(ThreadLocalRandom.current().nextInt(customerIds.size()));
        }

        return ContactRequest.builder()
                .contactName(generateRandomContactName()) // ✨ 3. 使用新的輔助方法
                .title(getRandomElement(TITLES))          // ✨ 3. 使用新的輔助方法
                .phone(generateRandomPhone())             // ✨ 3. 使用新的輔助方法
                .email(generateCustomEmail())   // (維持不變)
                .notes(getRandomElement(NOTES))           // ✨ 3. 使用新的輔助方法
                .customerId(customerId)
                .build();
    }

    /**request.setCustomerEmail(generateRandomEmail());
     * 產生指定數量的假聯絡人請求。
     * @param count 要產生的請求數量
     * @param customerIds 可選的客戶ID列表，用於關聯聯絡人。
     * @return 包含假聯絡人請求的列表
     */
    public List<ContactRequest> generateFakeContactRequests(int count, List<Long> customerIds) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateFakeContactRequest(customerIds))
                .collect(Collectors.toList());
    }

    // --- ✨ 4. 全新增加的私有輔助方法 ---

    private String generateRandomContactName() {
        String lastName = getRandomElement(LAST_NAMES);
        // 隨機 2~3 個中文字
        int firstNameLength = ThreadLocalRandom.current().nextInt(2, 4);
        StringBuilder firstName = new StringBuilder();
        for (int i = 0; i < firstNameLength; i++) {
            int index = ThreadLocalRandom.current().nextInt(COMMON_CHINESE_CHARS.length());
            firstName.append(COMMON_CHINESE_CHARS.charAt(index));
        }
        return lastName + firstName;
    }

    private String generateRandomPhone() {
        String prefix = getRandomElement(PHONE_PREFIXES);
        String suffix;
        if ("09".equals(prefix)) {
            // 手機號碼，後綴 8 碼
            suffix = faker.number().digits(8);
        } else {
            // 市話，後綴 7 碼
            suffix = faker.number().digits(7);
        }
        return prefix + "-" + suffix;
    }

    private <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }
}
