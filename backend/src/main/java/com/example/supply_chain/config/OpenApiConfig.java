package com.example.supply_chain.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI supplyChainOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/").description("Default Server"))
                .info(new Info()
                .title("Supply Chain API")
                .description("CRUD endpoints for supply chain entities as per ERD")
                .version("v1"));
    }
}


