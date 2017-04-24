package com.zespolowka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@SpringBootApplication
@ComponentScan
@EnableCaching
public class AluminiumApplication {
    private static final Logger logger = LoggerFactory.getLogger(AluminiumApplication.class);

    public AluminiumApplication() {
    }

    public static void main(String... args) {
        SpringApplication.run(AluminiumApplication.class, args);
        logger.info("Aplikacja uruchomiona");
    }

    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

}
