package com.coding.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI catalogServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("eSupermarket - Catalog Service API")
                        .description("REST API documentation for Catalog Service managing products, categories, brands, suppliers, tags, and product groups.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("eSupermarket Engineering")
                                .email("engineering@esupermarket.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
