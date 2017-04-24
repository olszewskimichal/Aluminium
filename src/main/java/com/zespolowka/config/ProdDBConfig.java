package com.zespolowka.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URISyntaxException;

/**
 * Created by Pitek on 2016-01-05.
 */
@Configuration
@Profile("prod")
public class ProdDBConfig {

    @Bean
    public BasicDataSource dataSource() throws URISyntaxException {
        String username = "zespolowka";
        String password = "CjTjBXNbHJCjar5p";
        String path = "46.238.244.222";
        String database = "/zespolowka";
        int port = 1500;
        String dbUrl = "jdbc:mysql://" + path + ':' + port + database;

        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(dbUrl + "?useUnicode=true&characterEncoding=utf-8");
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return basicDataSource;
    }
}
