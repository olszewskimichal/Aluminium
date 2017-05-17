package com.zespolowka.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.zespolowka.entity.Notification;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.service.NotificationService;
import com.zespolowka.validators.SendMessageValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class NotificationControllerTest {
	NotificationController controller;

	@Mock
	Model model;

	@Mock
	NotificationService notificationService;
	@Mock
	SendMessageValidator messageValidator;
	@Mock
	private BindingResult bindingResult;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new NotificationController(notificationService, messageValidator);
	}

	@Test
	public void shouldShowNotifications() throws Exception {
		//given
		Notification notification = new Notification();

		assertThat(controller.showNotifications(model, notification)).isEqualTo("messages");
	}

	@Test
	public void shouldReadNotificationAndRedirect() throws Exception {
		//given
		RedirectAttributes mock = Mockito.mock(RedirectAttributes.class);
		//when
		when(notificationService.readNotification(1)).thenReturn(new Notification());

		assertThat(controller.readNotification(1, mock)).isEqualTo("redirect:/messages");
	}

	@Test
	public void shouldShowNewMessageForm() throws Exception {
		assertThat(controller.newMessage(model)).isEqualTo("sendMessage");
	}

	@Test
	public void shouldSendMessageWithCorrectMsg() throws Exception {
		//given
		NewMessageForm newMessageForm = new NewMessageForm();

		assertThat(controller.sendMessage(model, newMessageForm, bindingResult)).isEqualTo("sendMessage");
		verify(notificationService).sendMessage(newMessageForm);
		verify(model).addAttribute("sukces", true);
		verify(model).addAttribute("newMessageForm", new NewMessageForm());
		verifyNoMoreInteractions(model);
	}

	@Test
	public void shouldShowAgainMessageFormWhenNotCorrectForm() {
		//given
		NewMessageForm newMessageForm = new NewMessageForm();
		given(bindingResult.hasErrors()).willReturn(true);
		//when
		assertThat(controller.sendMessage(model, newMessageForm, bindingResult)).isEqualTo("sendMessage");
		//then
		verify(model).addAttribute("newMessageForm", newMessageForm);
		verifyNoMoreInteractions(model);
	}

}