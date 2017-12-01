package com.example.kalah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.ServletContextRequestLoggingFilter;

@SpringBootApplication
@EnableAutoConfiguration(exclude = JacksonAutoConfiguration.class)
public class KalahApp {

    @Bean
    public AbstractRequestLoggingFilter getLoggingFilter() {
        return new ServletContextRequestLoggingFilter();
    }

    public static void main(String[] args) {
        SpringApplication.run(KalahApp.class, args);
    }
}
