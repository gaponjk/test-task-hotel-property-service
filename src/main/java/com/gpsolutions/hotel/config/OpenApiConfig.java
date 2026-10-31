package com.gpsolutions.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API Application")
                        .version("1.0.0")
                        .description("REST API service for hotels")
                        .contact(new Contact()
                                .name("Yaraslau Haponenka")
                                .url("https://github.com/gaponjk")));
    }
}