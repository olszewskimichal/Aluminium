package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.repository.SolutionTestRepository;
import com.zespolowka.service.NotificationService;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.ChangePasswordValidator;
import com.zespolowka.validators.UserEditValidator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class UserControllerTest {

	@Mock
	ChangePasswordValidator changePasswordValidator;
	@Mock
	UserEditValidator userEditValidator;
	@Mock
	SolutionTestRepository solutionTestRepository;
	@Mock
	Model model;
	private UserController controller;
	@Mock
	private UserService service;
	@Mock
	private NotificationService notificationService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new UserController(service, userEditValidator);
	}

	@Test
	public void shouldShowUserDetail() {
		//given
		given(service.getCurrentUser()).willReturn(new User());
		//when
		//then
		assertThat(controller.editCurrentUserDetail(model)).isEqualTo("userEdit");
		verify(model).addAttribute("userEditForm", new UserEditForm(new User()));
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldEditCurrentUserDetails() {
		//given
		Errors errors = Mockito.mock(Errors.class);
		given(errors.hasErrors()).willReturn(false);
		//when
		//then
		assertThat(controller.saveCurrentUser(new UserEditForm(), errors, model)).isEqualTo("userEdit");
		verify(model).addAttribute("sukces", true);
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldFailedEditingCurrentUser() {
		//given
		Errors errors = Mockito.mock(Errors.class);
		given(errors.hasErrors()).willReturn(true);
		//when
		//then
		assertThat(controller.saveCurrentUser(new UserEditForm(), errors, model)).isEqualTo("userEdit");
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldDeleteUserById() {
		//given
		given(service.deleteUserById(Mockito.anyLong())).willReturn(true);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		//when
		assertThat(controller.deleteUser(Mockito.anyLong(), redirectAttributes)).isEqualTo("redirect:/users");
		//then
		verify(redirectAttributes).addFlashAttribute("sukces", true);
		verify(redirectAttributes).addFlashAttribute("message", "Usunieto uzytkownika o id " + 0);
		verifyNoMoreInteractions(redirectAttributes);
	}


	@Test
	public void shouldNotDeleteUserById() {
		//given
		given(service.deleteUserById(Mockito.anyLong())).willReturn(false);
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		//when
		assertThat(controller.deleteUser(Mockito.anyLong(), redirectAttributes)).isEqualTo("redirect:/users");
		//then
		verify(redirectAttributes).addFlashAttribute("blad", true);
		verify(redirectAttributes).addFlashAttribute("message", "Nieprawidłowa operacja");
		verifyNoMoreInteractions(redirectAttributes);
	}

	@Test
	public void shouldShowChangeActiveMessage() {
		//given
		RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
		given(service.changeUserActivity(1L)).willReturn(new ImmutablePair<>(true, "komunikat"));
		//when
		//then
		assertThat(controller.activateUser(1, redirectAttributes)).isEqualTo("redirect:/users");
		verify(redirectAttributes).addFlashAttribute("sukces", true);
		verify(redirectAttributes).addFlashAttribute("message", "komunikat");
	}

}