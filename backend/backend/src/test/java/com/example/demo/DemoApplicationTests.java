package com.example.demo;

import com.example.demo.security.JwtToolTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class DemoApplicationTests {

	// 測試用的 JWT 金鑰在執行時隨機產生，不依賴開發者電腦上的 JWT_SECRET
	@DynamicPropertySource
	static void jwtSecret(DynamicPropertyRegistry registry) {
		registry.add("jwt.secret", () -> JwtToolTest.randomBase64Key(32));
	}

	@Test
	void contextLoads() {
	}

}
