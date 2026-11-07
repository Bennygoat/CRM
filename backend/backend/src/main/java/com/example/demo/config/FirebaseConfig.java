package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    private static final String KEY_FILE = "firebase-adminsdk.json";

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(KEY_FILE)) {
            if (serviceAccount == null) {
                log.warn("找不到 {}，已停用 Firebase 登入，其他功能不受影響", KEY_FILE);
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (IOException e) {
            throw new RuntimeException("Firebase 初始化失敗", e);
        }
    }
}
