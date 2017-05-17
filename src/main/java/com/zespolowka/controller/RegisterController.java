package com.zespolowka.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.service.UserService;
import com.zespolowka.service.VerificationTokenService;
import com.zespolowka.validators.UserCreateValidator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Pitek on 2015-11-30.
 */
@Controller
@Slf4j
public class RegisterController {

	private static final String USER_CREATE_FORM = "userCreateForm";
	private static final String REGISTER = "register";
	private static final String LOGIN = "login";
	@Autowired
	private final UserService userService;

	@Autowired
	private final UserCreateValidator userCreateValidator;

	@Autowired
	private final VerificationTokenService verificationTokenService;


	public RegisterController(UserService userService, UserCreateValidator userCreateValidator, VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.userCreateValidator = userCreateValidator;
		this.verificationTokenService = verificationTokenService;
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerPage(Model model) {
		log.info("nazwa metody = registerPage");
		model.addAttribute(USER_CREATE_FORM, new UserCreateForm());
		return REGISTER;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerSubmit(@ModelAttribute @Valid UserCreateForm userCreateForm, BindingResult result, Model model, HttpServletRequest servletRequest) throws MessagingException {
		log.info("nazwa metody = registerSubmit");
		userCreateValidator.validate(userCreateForm, result);
		if (result.hasErrors()) {
			log.info(userCreateForm.toString());
			model.addAttribute(USER_CREATE_FORM, userCreateForm);
			return REGISTER;
		}
		else {
			userService.create(userCreateForm, servletRequest.getRequestURL().toString());
			model.addAttribute(USER_CREATE_FORM, new UserCreateForm());
			model.addAttribute("confirmRegistration", true);
			return REGISTER;
		}
	}


	@RequestMapping(value = "/register/registrationConfirm", method = RequestMethod.GET)
	public String confirmRegistration(Model model, @RequestParam("token") String token) {
		log.info("Potwierdzenie rejestacji");
		model.addAttribute(verificationTokenService.confirmRegistrationToken(token), true);
		return LOGIN;
	}

}
