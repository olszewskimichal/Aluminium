package com.zespolowka.validators;

import com.zespolowka.forms.UserEditForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ChangePasswordValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordValidator.class);

    public ChangePasswordValidator() {
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserEditForm.class);
    }

    public void validate(Object target, Errors errors) {
        UserEditForm form = (UserEditForm) target;
        logger.info("Walidacja hasla {}", target);
        if (!form.getPassword().isEmpty()) {
            if (!form.getPassword().equals(form.getConfirmPassword())) {
                errors.rejectValue("password", "password_error");
            }
            if (!form.getPassword()
                    .matches("^(?=.*?[A-Z])(?=(.*[a-z]){1,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$")) {
                errors.rejectValue("password", "passwordlength_error");
            }
        }
    }
}
