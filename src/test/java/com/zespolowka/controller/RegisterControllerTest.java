package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.service.UserService;
import com.zespolowka.service.VerificationTokenService;
import com.zespolowka.validators.UserCreateValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public class RegisterControllerTest {
	RegisterController controller;

	@Mock
	Model model;

	@Mock
	UserService userService;
	@Mock
	UserCreateValidator validator;
	@Mock
	private BindingResult bindingResult;

	@Mock
	private VerificationTokenService tokenService;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new RegisterController(userService, validator, tokenService);
	}

	@Test
	public void shouldShowRegisterPage() throws Exception {
		//given

		//when
		//then
		assertThat(controller.registerPage(model)).isEqualTo("register");
		verify(model).addAttribute("userCreateForm", new UserCreateForm());
	}

	@Test
	public void shouldCorrectSubmitRegister() throws Exception {
		given(bindingResult.hasErrors()).willReturn(false);
		HttpServletRequest request = new MockHttpServletRequest();
		request.getRequestURL().append("localhost");

		assertThat(controller.registerSubmit(new UserCreateForm(), bindingResult, model, request)).isEqualTo("register");
		verify(model).addAttribute("userCreateForm", new UserCreateForm());
		verify(model).addAttribute("confirmRegistration", true);
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldNotSubmitIncorrectRegisterForm() throws MessagingException {
		//given
		given(bindingResult.hasErrors()).willReturn(true);
		HttpServletRequest request = new MockHttpServletRequest();
		UserCreateForm userCreateForm = new UserCreateForm();
		userCreateForm.setEmail("test");
		//when
		//then
		assertThat(controller.registerSubmit(userCreateForm, bindingResult, model, request)).isEqualTo("register");
		verify(model).addAttribute("userCreateForm", userCreateForm);
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldConfirmRegistration() throws Exception {
		//given
		given(tokenService.confirmRegistrationToken(Mockito.anyString())).willReturn("msg");
		//when
		//then
		assertThat(controller.confirmRegistration(model, Mockito.anyString())).isEqualTo("login");
		verify(model).addAttribute("msg", true);
		verifyNoMoreInteractions(model);
	}

}