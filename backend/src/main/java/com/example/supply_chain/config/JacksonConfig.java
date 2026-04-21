package com.example.supply_chain.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Jackson to handle Hibernate lazy-loaded proxies gracefully.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Module hibernateModule() {
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();
        module.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
        return module;
    }
}


