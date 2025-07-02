package com.example.demo.util;

import com.example.demo.dto.request.CustomerTagRequest;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CustomerTagFaker {
    private final Faker faker = new Faker();
    private final Random random = new Random();

    private static final String[] PREDEFINED_TAG_NAMES = {
            "VIP客戶", "潛在客戶", "新客戶", "戰略合作", "一般客戶", "流失客戶", "高價值"
    };

    // 定義一組顏色供隨機選取
    private static final String[] PREDEFINED_COLORS = {
            "#D4EDDA", // 淺綠
            "#D1ECF1", // 淺藍
            "#F8D7DA", // 淺紅
            "#FFF3CD", // 淺黃
            "#E9ECEF", // 淺灰
            "#B1A0C7", // 淺紫
            "#FADBD8"  // 粉紅
    };

    /**
     * 生成指定數量的假「客戶標籤」請求。
     * 標籤名稱從預定義列表中隨機選取，保證不重複。
     *
     * @param count 要生成的標籤數量
     * @return 假客戶標籤請求列表
     * @throws IllegalArgumentException 如果請求生成的標籤數量超過預定義名稱的數量
     */
    public List<CustomerTagRequest> generateFakeTagRequests(int count) {
        if (count > PREDEFINED_TAG_NAMES.length) {
            throw new IllegalArgumentException("請求生成的客戶標籤數量 (" + count +
                    ") 超過預定義的名稱數量 (" +
                    PREDEFINED_TAG_NAMES.length + ")。");
        }

        List<CustomerTagRequest> requests = new ArrayList<>();

        // 複製並打亂預定義的名稱列表，以便隨機且不重複地選取
        List<String> availableTagNames = new ArrayList<>(List.of(PREDEFINED_TAG_NAMES));
        Collections.shuffle(availableTagNames, random);

        // 複製並打亂顏色列表
        List<String> shuffledColors = new ArrayList<>(List.of(PREDEFINED_COLORS));
        Collections.shuffle(shuffledColors, random);

        for (int i = 0; i < count; i++) {
            // 從打亂後的列表中依序取用，保證唯一性
            String tagName = availableTagNames.get(i);
            // 隨機選取一個顏色，如果顏色用完則循環使用
            String color = shuffledColors.get(i % shuffledColors.size());

            requests.add(CustomerTagRequest.builder()
                    .tagName(tagName)
                    .color(color)
                    .build());
        }
        return requests;
    }
}
