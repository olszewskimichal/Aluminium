package com.zespolowka.config;


import com.zespolowka.entity.user.User;
import com.zespolowka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    private final UserService userService;

    public CustomAuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
        String email = httpServletRequest.getParameter("username");
        logger.info("Pomyslne logowanie uzytkownika " + email);
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Uzytkownik z mailem=%s nie istnieje", email)));
        user.setLogin_tries(3);
        userService.update(user);
    }

}
