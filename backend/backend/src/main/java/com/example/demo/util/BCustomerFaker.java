package com.example.demo.util;

import com.example.demo.dto.request.BCustomerRequest;
import com.example.demo.enums.BCustomerIndustry;
import com.example.demo.enums.BCustomerLevel;
import com.example.demo.enums.BCustomerType;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class BCustomerFaker {

    private final Faker faker;

    // --- 定義假資料的常數 ---
    // 公司名稱相關
    private static final String[] COMPANY_INDUSTRY_SUFFIXES = {"資訊", "生技", "醫美", "食品", "工業", "電子","食材","餐飲"};
    private static final String[] COMPANY_TYPE_SUFFIXES = {"有限公司", "股份有限公司", "企業社"};
    private static final String COMMON_CHINESE_CHARS = "景天大業華成宏達豐盛元亨利貞昌"; // 用於產生公司名的隨機中文字

    // 地址相關
    private static final String[] CITIES = {"台北市", "台中市", "台南市", "高雄市", "桃園市"};
    private static final String[] DISTRICTS = {"南屯區", "西區", "南區", "大雅區", "前鎮區", "平鎮區", "楊梅區", "永和區"};
    private static final String[] STREETS = {"忠孝路", "中正路", "大港路", "資展路", "大連路", "大進路", "公益路"};
    private static final String[] SECTIONS = {"一段", "二段", "三段"};

    // Email & 電話相關
    private static final String[] EMAIL_DOMAINS = {"gmail.com", "me.com", "yahoo.com", "hotmail.com"};
    private static final String[] TEL_AREA_CODES = {"02", "03", "04", "05", "07"};


    public BCustomerFaker() {
        // 使用台灣語系，讓產生的名字、地址等更在地化
        this.faker = new Faker(new Locale("zh-TW"));
    }

    /**
     * 產生一筆包含逼真資料的 BCustomerRequest。
     * @return BCustomerRequest 物件
     */
    public BCustomerRequest generateFakeBCustomerRequest() {
        BCustomerRequest request = new BCustomerRequest();

        // ✨ 1. 產生符合格式的公司名稱
        request.setCustomerName(generateRandomCompanyName());

        // ✨ 2. 產生符合格式的 Email
        request.setCustomerEmail(generateRandomEmail());

        // ✨ 3. 產生符合格式的地址
        request.setCustomerAddress(generateRandomAddress());

        // ✨ 4. 產生符合格式的電話
        request.setCustomerTel(generateRandomTel());

        // 產生統一編號 (維持不變)
        request.setTinNumber(faker.number().digits(8));

        // 隨機選擇枚舉類型 (維持不變)
        request.setIndustry(getRandomEnum(BCustomerIndustry.class));

        return request;
    }

    /**
     * 產生指定數量的 BCustomerRequest 列表。
     * @param count 數量
     * @return List<BCustomerRequest>
     */
    public List<BCustomerRequest> generateFakeBCustomerRequests(int count) {
        List<BCustomerRequest> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requests.add(generateFakeBCustomerRequest());
        }
        return requests;
    }

    // --- 私有輔助方法 ---

    private String generateRandomCompanyName() {
        StringBuilder nameBuilder = new StringBuilder();
        // 隨機 2~4 個中文字
        int charCount = ThreadLocalRandom.current().nextInt(2, 5);
        for (int i = 0; i < charCount; i++) {
            int index = ThreadLocalRandom.current().nextInt(COMMON_CHINESE_CHARS.length());
            nameBuilder.append(COMMON_CHINESE_CHARS.charAt(index));
        }
        // 加上產業和公司類型
        nameBuilder.append(getRandomElement(COMPANY_INDUSTRY_SUFFIXES));
        nameBuilder.append(getRandomElement(COMPANY_TYPE_SUFFIXES));
        return nameBuilder.toString();
    }

    private String generateRandomAddress() {
        return getRandomElement(CITIES) +
                getRandomElement(DISTRICTS) +
                getRandomElement(STREETS) +
                getRandomElement(SECTIONS) +
                ThreadLocalRandom.current().nextInt(1, 446) + "號";
    }

    private String generateRandomEmail() {
        // 使用 faker 產生 8 碼的英數混合字串
        String localPart = faker.lorem().characters(8, true, true).toLowerCase();
        String domainPart = getRandomElement(EMAIL_DOMAINS);
        return localPart + "@" + domainPart;
    }

    private String generateRandomTel() {
        String areaCode = getRandomElement(TEL_AREA_CODES);
        // 使用 faker 產生 8 位數字
        String number = faker.number().digits(8);
        return areaCode + "-" + number;
    }

    private <T> T getRandomElement(T[] array) {
        return array[ThreadLocalRandom.current().nextInt(array.length)];
    }

    private <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}