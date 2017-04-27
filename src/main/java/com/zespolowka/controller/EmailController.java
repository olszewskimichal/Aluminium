package com.zespolowka.controller;

import java.util.Date;

import com.zespolowka.entity.Notification;
import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class EmailController {
	private NotificationService notificationService;

	@Autowired
	public EmailController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@RequestMapping("/sendMessage")
	public void sendMessage() {
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User sender = currentUser.getUser();
		notificationService.createNotification(new Notification("GRUPOWA", "Temat", new Date(), Role.ADMIN, sender));
	}
}
