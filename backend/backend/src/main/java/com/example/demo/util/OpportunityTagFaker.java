package com.example.demo.util;

import com.example.demo.dto.request.OpportunityTagRequest;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OpportunityTagFaker {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    private static final String[] PREDEFINED_TAG_NAMES = {
            "success", "processing", "error", "warning", "default"
    };

    private static final String[] PREDEFINED_COLORS = {
            "#D4EDDA", // success (淺綠)
            "#D1ECF1", // processing (淺藍)
            "#F8D7DA", // error (淺紅)
            "#FFF3CD", // warning (淺黃)
            "#E9ECEF", // default (淺灰)
    };

    /**
     * 生成指定數量的假「商機標籤」請求。
     * 標籤名稱從預定義列表中隨機選取，保證不重複。
     *
     * @param count 要生成的標籤數量
     * @return 假商機標籤請求列表
     * @throws IllegalArgumentException 如果請求生成的標籤數量超過預定義名稱的數量
     */
    public List<OpportunityTagRequest> generateFakeTagRequests(int count) {
        if (count > PREDEFINED_TAG_NAMES.length) {
            throw new IllegalArgumentException("請求生成的商機標籤數量 (" + count +
                    ") 超過預定義的名稱數量 (" +
                    PREDEFINED_TAG_NAMES.length + ")。");
        }

        List<OpportunityTagRequest> requests = new ArrayList<>();

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

            // *** 修改：建立 OpportunityTagRequest ***
            requests.add(OpportunityTagRequest.builder()
                    .tagName(tagName)
                    .color(color)
                    .build());
        }
        return requests;
    }
}
