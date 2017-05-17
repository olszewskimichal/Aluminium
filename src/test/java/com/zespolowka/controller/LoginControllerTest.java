package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import com.zespolowka.service.SendMailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.WebAttributes;
import org.springframework.ui.Model;

public class LoginControllerTest {


	private LoginController controller;

	@Mock
	private SendMailService mailService;

	@Mock
	private Model model;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = new LoginController(mailService);
	}

	@Test
	public void shouldReturnLoginPage() throws Exception {
		assertThat(controller.getLoginPage(model, Optional.empty())).isEqualTo("login");
		verify(model).addAttribute("error", Optional.empty());
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldReturnErrorLoginPage() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		HttpSession session = request.getSession();
		session.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, "Nieprawidłowy użytkownik");
		assertThat(controller.getErrorLoginPage(model, request)).isEqualTo("login");
		verify(model).addAttribute("loginError", true);
		verify(model).addAttribute("errorMessage", request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldReturnExpiredLoginPage() throws Exception {
		assertThat(controller.getExpiredLoginPage(model)).isEqualTo("login");
		verify(model).addAttribute("getExpiredLoginPage", true);
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldReturnRemindPasswordPage() throws Exception {
		assertThat(controller.getRemindPasswordPage()).isEqualTo("remindPassword");
	}

	@Test
	public void shouldSendRemindPassword() throws Exception {
		HttpServletRequest request = new MockHttpServletRequest();
		assertThat(controller.sendRemindPassword(request, model)).isEqualTo("remindPassword");
		verify(model).addAttribute("sukces", true);
		verifyNoMoreInteractions(model);
	}

}