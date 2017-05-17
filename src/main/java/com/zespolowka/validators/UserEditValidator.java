package com.zespolowka.validators;

import static com.zespolowka.entity.user.Role.SUPERADMIN;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@Slf4j
public class UserEditValidator implements Validator {
	private final UserService userService;
	private final ChangePasswordValidator passwordValidator;

	@Autowired
	public UserEditValidator(UserService userService, ChangePasswordValidator passwordValidator) {
		this.userService = userService;
		this.passwordValidator = passwordValidator;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(UserEditForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = currentUser.getUser();
		UserEditForm form = (UserEditForm) target;
		log.info("Walidacja edycji{}", target);

		if (form.getPassword() == null || form.getConfirmPassword() == null)
			errors.rejectValue("passHash", "password_error");
		else if (!form.getPassword().equals(form.getConfirmPassword())) {
			errors.rejectValue("passHash", "password_error");
		}
		if (!user.getEmail().equals(form.getEmail()) && userService.getUserByEmail(form.getEmail()).isPresent()) {
			errors.rejectValue("email", "email_error");
		}
		if (!user.getId().equals(form.getId())) {
			errors.rejectValue("id", "id_error");
		}
		if (!form.getRole().equals(user.getRole()) && !user.getRole().equals(SUPERADMIN)) {
			errors.rejectValue("role", "role_error");
		}
		passwordValidator.validate(form, errors);
	}

}

