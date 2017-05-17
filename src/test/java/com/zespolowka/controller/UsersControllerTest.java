package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;

import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.UsersEditValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;

public class UsersControllerTest {

	private UsersController controller;
	@Mock
	private UserService service;

	@Mock
	private UsersEditValidator usersEditValidator;

	@Mock
	private Model model;

	@Mock
	private Errors errors;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controller = new UsersController(service, usersEditValidator);
	}

	@Test
	public void shouldShowUsersPage() {
		//given
		given(service.getAllUsers()).willReturn(new ArrayList<>());
		//when
		//then
		assertThat(controller.getUsersPage(model)).isEqualTo("users");
		verify(model).addAttribute("Users", new ArrayList<>());
		verify(model).addAttribute("usersEditForm", new UserEditForm(new User()));
	}

	@Test
	public void shouldEditUserAndRedirect() {
		//given
		given(errors.hasErrors()).willReturn(false);
		//when
		//then
		assertThat(controller.saveEditedUser(new UserEditForm(), errors, model)).isEqualTo("redirect:/users");
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldNotEditUserWhenFormIsInvalid() {
		//given
		given(errors.hasErrors()).willReturn(true);
		//when
		//then
		assertThat(controller.saveEditedUser(new UserEditForm(), errors, model)).isEqualTo("redirect:/users");
		verify(model).addAttribute("errorOnEditUser", true);
		verifyNoMoreInteractions(model);
	}

}
