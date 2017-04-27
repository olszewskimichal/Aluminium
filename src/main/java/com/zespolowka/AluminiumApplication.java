package com.zespolowka;

import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableCaching
@Slf4j
public class AluminiumApplication {

	public static void main(String... args) {
		SpringApplication.run(AluminiumApplication.class, args);
		log.info("Aplikacja uruchomiona");
	}

	@Bean
	public Java8TimeDialect java8TimeDialect() {
		return new Java8TimeDialect();
	}

}
