package com.zespolowka.config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

import com.zespolowka.entity.user.User;
import com.zespolowka.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;


@Component
@Slf4j
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final MessageSource messages;
	private final LocaleResolver localeResolver;
	private final UserService userService;

	public CustomAuthenticationFailureHandler(MessageSource messages, LocaleResolver localeResolver, UserService userService) {
		this.messages = messages;
		this.localeResolver = localeResolver;
		this.userService = userService;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.info("Bledna autoryzacja uzytkownika");
		log.info(exception.getMessage());
		if (exception instanceof BadCredentialsException) {

			String email = request.getParameter("username");
			try {
				User user = userService.getUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("Uzytkownik z mailem=%s nie istnieje", email)));
				int tries = user.getLoginTries();
				tries--;
				if (tries > 0) {
					user.setLoginTries(tries);
					log.info("Tries:{}", user.getLoginTries());
					userService.update(user);
				}
				else {
					user.setAccountNonLocked(false);
					userService.update(user);
					log.info("User blocked");
				}
			}
			catch (UsernameNotFoundException e) {
				log.error("Nieprawidlowy uzytkownik");
			}
		}

		setDefaultFailureUrl("/login-error");
		super.onAuthenticationFailure(request, response, exception);
		Locale locale = localeResolver.resolveLocale(request);
		String errorMessage = messages.getMessage("message.badCredentials", null, locale);

		if ("user is disabled".equalsIgnoreCase(exception.getMessage())) {
			errorMessage = messages.getMessage("auth.message.disabled", null, locale);
		}
		else if ("User account is locked".equalsIgnoreCase(exception.getMessage())) {
			errorMessage = messages.getMessage("auth.message.blocked", null, locale);
		}
		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
	}
}
