package com.example.demo.config.util;

import com.example.demo.dto.request.CCustomerRegisterRequest;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Component
public class CCustomerFaker {

    private final Faker faker;

    public CCustomerFaker() {
        this.faker = new Faker(new Locale("zh-TW"));
    }

    public CCustomerRegisterRequest generateFakeCCustomerRequest() {
        CCustomerRegisterRequest request = new CCustomerRegisterRequest();
        request.setAccount(faker.internet().emailAddress());
        request.setPassword(faker.internet().password(8, 16, true, true, true));
        request.setCustomerName(faker.name().fullName());
        request.setBirthday(faker.date().birthday(18, 65).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        request.setEmail(request.getAccount()); // Use account email as customer email
//        request.setCustomerTel(faker.phoneNumber().cellPhone());
        request.setAddress(faker.address().fullAddress());
        // isActive and isDeleted are usually handled by default values or system logic, not set by request
        return request;
    }

    public List<CCustomerRegisterRequest> generateFakeCCustomerRequests(int count) {
        List<CCustomerRegisterRequest> requests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            requests.add(generateFakeCCustomerRequest());
        }
        return requests;
    }
}
