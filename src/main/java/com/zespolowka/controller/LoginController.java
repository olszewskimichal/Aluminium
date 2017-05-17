package com.zespolowka.controller;


import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import com.zespolowka.service.SendMailService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@Slf4j
public class LoginController {
	private static final String LOGIN = "login";
	private final SendMailService sendMailService;

	@Autowired
	public LoginController(SendMailService sendMailService) {
		this.sendMailService = sendMailService;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String getLoginPage(Model model, @RequestParam Optional<String> error) {
		log.info("nazwa metody = getLoginPage");
		model.addAttribute("error", error);
		return LOGIN;
	}

	@RequestMapping("/login-error")
	public String getErrorLoginPage(Model model, HttpServletRequest request) {
		log.info("nazwa metody = getErrorLoginPage");
		model.addAttribute("loginError", true);
		model.addAttribute("errorMessage", request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
		return LOGIN;
	}

	@RequestMapping("/login-expired")
	public String getExpiredLoginPage(Model model) {
		log.info("nazwa metody = getErrorLoginPage");
		model.addAttribute("getExpiredLoginPage", true);
		return LOGIN;
	}

	@RequestMapping(value = "/remindPassword", method = RequestMethod.GET)
	public String getRemindPasswordPage() {
		log.info("nazwa metody = getRemindPasswordPage");
		return "remindPassword";
	}

	@RequestMapping(value = "/remindPassword", method = RequestMethod.POST)
	public String sendRemindPassword(HttpServletRequest request, Model model) {
		log.info("nazwa metody = sendRemindPassword");
		String email = request.getParameter("username");
		sendMailService.sendReminderMail(email);
		model.addAttribute("sukces", true);
		return "remindPassword";
	}

}
