package com.zespolowka.service;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by Pitek on 2015-12-11.
 */
@Service
@Slf4j
public class CurrentUserDetailsService implements UserDetailsService {
    private final UserService UserService;

    @Autowired
    public CurrentUserDetailsService(UserService UserService) {
        this.UserService = UserService;
    }

    @Override
    public CurrentUser loadUserByUsername(String email) {
        log.info("Autentykacja uzytkownika o mailu = {}", email);
        try {
            User user = UserService.getUserByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("Uzytkownik z mailem=%s nie istnieje", email)));
            return new CurrentUser(user);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}