package com.example.demo.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 定義一個 SecurityScheme，名稱為 "jwt_auth"
        final SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Security type is HTTP
                .scheme("bearer")               // Scheme is Bearer
                .bearerFormat("JWT")            // Format is JWT
                .in(SecurityScheme.In.HEADER)   // Location is in the header
                .name("Authorization");         // Header name is "Authorization"

        // 定義 API 的基本資訊
        final Info info = new Info()
                .title("CMS＋ERP＋CRM系統 API")
                .version("1.0.0")
                .description("這是一份此專案的 RESTful API 文件。");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("jwt_auth"))
                .components(new Components().addSecuritySchemes("jwt_auth", securityScheme));
    }
}
