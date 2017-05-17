package com.zespolowka.controller;

import com.zespolowka.entity.Notification;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.service.NotificationService;
import com.zespolowka.validators.SendMessageValidator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
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
	private static final String NEW_MESSAGE_FORM = "newMessageForm";
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
		model.addAttribute("idActiveNotification", notification.getId());
		return "messages";
	}

	@RequestMapping(value = "/messages/{id}", method = RequestMethod.GET)
	public String readNotification(@PathVariable final Integer id, final RedirectAttributes redirectAttributes) {
		log.info("nazwa metody = readNotification");
		redirectAttributes.addFlashAttribute("Notification", notificationService.readNotification(id));
		return "redirect:/messages";
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
	public String newMessage(final Model model) {
		log.info("nazwa metody = newMessage");
		model.addAttribute(NEW_MESSAGE_FORM, new NewMessageForm());
		return SEND_MESSAGE;
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public String sendMessage(final Model model, @ModelAttribute final NewMessageForm newMessageForm, BindingResult errors) {
		log.info("nazwa metody = sendMessage");
		sendMessageValidator.validate(newMessageForm, errors);
		if (errors.hasErrors()) {
			model.addAttribute(NEW_MESSAGE_FORM, newMessageForm);
			return SEND_MESSAGE;
		}
		else {
			notificationService.sendMessage(newMessageForm);
			log.info("Przeszly maile");
			model.addAttribute("sukces", true);
			model.addAttribute(NEW_MESSAGE_FORM, new NewMessageForm());
			return SEND_MESSAGE;
		}
	}
}