package com.zespolowka.validators;

import com.zespolowka.forms.UserEditForm;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
@Slf4j
public class ChangePasswordValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(UserEditForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserEditForm form = (UserEditForm) target;
		log.info("Walidacja hasla {}", target);
		if (!form.getPassword().isEmpty()) {
			if (!form.getPassword().equals(form.getConfirmPassword())) {
				errors.rejectValue("passHash", "password_error");
			}
			if (!form.getPassword().matches("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$")) {
				errors.rejectValue("passHash", "passwordlength_error");
			}
		}
	}
}
