package com.zespolowka.config;

import com.zespolowka.entity.user.User;
import com.zespolowka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;


@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    @Autowired
    private MessageSource messages;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private UserService UserService;

    public CustomAuthenticationFailureHandler() {
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
        logger.info("Bledna autoryzacja uzytkownika");
        logger.info(exception.getMessage());
        if (exception instanceof BadCredentialsException) {

            String email = request.getParameter("username");
            try {
                User user = UserService.getUserByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException(String.format("Uzytkownik z mailem=%s nie istnieje", email)));
                int tries = user.getLogin_tries();
                tries--;
                if (tries > 0) {
                    user.setLogin_tries(tries);
                    logger.info("Tries:{}", user.getLogin_tries());
                    UserService.update(user);
                } else {
                    user.setAccountNonLocked(false);
                    UserService.update(user);
                    logger.info("User blocked");
                }
            } catch (Exception e) {
                logger.info(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        setDefaultFailureUrl("/login-error");
        super.onAuthenticationFailure(request, response, exception);
        Locale locale = localeResolver.resolveLocale(request);
        String errorMessage = messages.getMessage("message.badCredentials", null, locale);

        if (exception.getMessage().equalsIgnoreCase("user is disabled")) {
            errorMessage = messages.getMessage("auth.message.disabled", null, locale);
        } else if (exception.getMessage().equalsIgnoreCase("User account is locked")) {
            errorMessage = messages.getMessage("auth.message.blocked", null, locale);
        }
        request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
