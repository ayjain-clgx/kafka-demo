package com.self.emailnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EmailNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailNotificationApplication.class, args);
    }

    @Bean
    RestTemplate getRestTempate() {
        return new RestTemplate();
    }
}
