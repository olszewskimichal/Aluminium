package com.zespolowka.controller;

import com.zespolowka.entity.Notification;
import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.service.NotificationService;
import com.zespolowka.validators.SendMessageValidator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Slf4j
public class NotificationController {
	private static final String SEND_MESSAGE = "sendMessage";
	private final NotificationService notificationService;
	private final SendMessageValidator sendMessageValidator;

	@Autowired
	public NotificationController(NotificationService notificationService, SendMessageValidator sendMessageValidator) {
		this.notificationService = notificationService;
		this.sendMessageValidator = sendMessageValidator;
	}


	@RequestMapping(value = "/messages", method = RequestMethod.GET)
	public String showNotifications(final Model model, @ModelAttribute("Notification") final Notification notification) {
		log.info("nazwa metody = showNotifications");
		try {
			model.addAttribute("idNotification", notification.getId());
		}
		catch (final RuntimeException e) {
			log.error(e.getMessage(), e);
		}
		return "messages";
	}

	@RequestMapping(value = "/messages/{id}", method = RequestMethod.GET)
	public String readNotification(@PathVariable final Integer id, final RedirectAttributes redirectAttributes) {
		log.info("nazwa metody = readNotification");
		Notification notif = notificationService.getNotificationById(id.longValue());
		notif.setUnread(false);
		notificationService.createNotification(notif);
		redirectAttributes.addFlashAttribute("Notification", notif);
		return "redirect:/messages";
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
	public String newMessage(final Model model) {
		log.info("nazwa metody = newMessage");
		model.addAttribute("newMessageForm", new NewMessageForm());
		return SEND_MESSAGE;
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public String sendMessage(final Model model, @ModelAttribute final NewMessageForm newMessageForm, BindingResult errors) {
		log.info("nazwa metody = sendMessage");
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		log.info("Curr:{}", currentUser.getUser());
		newMessageForm.setSender(currentUser.getUser());
		sendMessageValidator.validate(newMessageForm, errors);
		if (errors.hasErrors()) {
			String err = errors.getAllErrors().get(0).toString();
			log.info("err:{}", err);
			try {
				notificationService.sendMessage(newMessageForm);
			}
			catch (final RuntimeException e) {
				log.info("\n{}" + '\n', model);
			}
			return SEND_MESSAGE;
		}
		else {
			notificationService.sendMessage(newMessageForm);
			log.info("Przeszly maile");
			model.addAttribute("sukces", true);
			model.addAttribute("newMessageForm", new NewMessageForm());
			return SEND_MESSAGE;
		}
	}
}