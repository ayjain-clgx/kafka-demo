package com.self.transfers.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class JpaConfig {

    @Bean("transactionManager")
    JpaTransactionManager jpaTransactionManager() {
        return new JpaTransactionManager();
    }
}
