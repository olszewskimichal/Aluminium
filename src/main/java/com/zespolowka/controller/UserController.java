package com.zespolowka.controller;

import javax.validation.Valid;

import com.zespolowka.forms.UserEditForm;
import com.zespolowka.service.UserService;
import com.zespolowka.validators.UserEditValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Pitek on 2015-12-01.
 */
@Controller
@RequestMapping(value = "/user")
@Slf4j
public class UserController {
	private static final String USER_EDIT = "userEdit";
	private static final String SUKCES = "sukces";
	private static final String MESSAGE = "message";
	private static final String BLAD = "blad";
	private static final String REDIRECT_USERS = "redirect:/users";
	private final UserService userService;
	private final UserEditValidator userEditValidator;


	@Autowired
	public UserController(final UserService userService, UserEditValidator userEditValidator) {
		this.userService = userService;
		this.userEditValidator = userEditValidator;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editCurrentUserDetail(final Model model) {
		log.info("nazwa metody = showCurrentUserDetail");
		model.addAttribute("userEditForm", new UserEditForm(userService.getCurrentUser()));
		return USER_EDIT;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String saveCurrentUser(@ModelAttribute @Valid final UserEditForm userEditForm, final Errors errors, final Model model) {
		log.info("nazwa metody = saveCurrentUser");
		userEditValidator.validate(userEditForm, errors);
		if (errors.hasErrors()) {
			log.info("err:{}", errors.getAllErrors());
			return USER_EDIT;
		}
		else {
			userService.editUser(userEditForm);
			model.addAttribute(SUKCES, true);
			return USER_EDIT;
		}
	}

	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String deleteUser(@PathVariable final Long id, RedirectAttributes redirectAttributes) {
		log.info("userDelete{}", id);
		//TODO pobrac wyjatek i na podstawie tego dac wiadomosc z błedem
		if (userService.deleteUserById(id)) {
			String usunieto = "Usunieto uzytkownika o id " + id;
			redirectAttributes.addFlashAttribute(SUKCES, true);
			redirectAttributes.addFlashAttribute(MESSAGE, usunieto);
		}
		else {
			redirectAttributes.addFlashAttribute(BLAD, true);
			redirectAttributes.addFlashAttribute(MESSAGE, "Nieprawidłowa operacja");
		}
		return REDIRECT_USERS;
	}


	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPERADMIN')")
	@RequestMapping(value = "/changeActive/{id}", method = RequestMethod.GET)
	public String activateUser(@PathVariable final Integer id, RedirectAttributes redirectAttributes) {
		log.debug("nazwa metody = activateUser");
		Pair<Boolean, String> userActivity = userService.changeUserActivity(id.longValue());
		redirectAttributes.addFlashAttribute(SUKCES, userActivity.getLeft());
		redirectAttributes.addFlashAttribute(MESSAGE, userActivity.getRight());
		return REDIRECT_USERS;
	}

}

