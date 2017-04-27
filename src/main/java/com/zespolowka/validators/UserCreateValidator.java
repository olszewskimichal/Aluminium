package com.zespolowka.validators;

import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Admin on 2015-12-03.
 */
@Component
@Slf4j
public class UserCreateValidator implements Validator {

	private final UserService userService;

	@Autowired
	public UserCreateValidator(UserService userService) {
		this.userService = userService;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(UserCreateForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserCreateForm form = (UserCreateForm) target;
		log.info("Walidacja {}", target);
		if (form.getPassHash() == null || form.getConfirmPassword() == null)
			errors.rejectValue("passHash", "password_error");
		else if (!form.getPassHash().equals(form.getConfirmPassword())) {
			errors.rejectValue("passHash", "password_error");
		}

		if (userService.getUserByEmail(form.getEmail()).isPresent()) {
			errors.rejectValue("email", "email_error");
		}
	}

	@Override
	public String toString() {
		return "UserCreateValidator{" + "UserServiceImpl=" + userService + '}';
	}
}
