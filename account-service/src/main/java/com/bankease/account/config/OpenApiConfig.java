package com.bankease.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BankEase — Account Service API")
                        .description("Core Banking Microservices Platform: Account Lifecycle & Balance Management API (Kotak Tech)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BankEase Engineering")
                                .email("dev@bankease.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
