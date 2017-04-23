package com.zespolowka.service;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Pitek on 2015-12-11.
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);
    private final UserService UserService;

    @Autowired
    public CurrentUserDetailsService(UserService UserService) {
        this.UserService = UserService;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) {
        logger.info("Autentykacja uzytkownika o mailu = {}", email);
        try {
            User user = UserService.getUserByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("Uzytkownik z mailem=%s nie istnieje", email)));
            return new CurrentUser(user);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "CurrentUserDetailsService{" +
                "UserServiceImpl=" + UserService +
                '}';
    }
}