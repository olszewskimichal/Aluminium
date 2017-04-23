package com.zespolowka.validators;

import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by Admin on 2015-12-03.
 */
@Component
public class UserCreateValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(UserCreateValidator.class);

    private final UserService UserService;

    @Autowired
    public UserCreateValidator(UserService UserService) {
        this.UserService = UserService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserCreateForm.class);
    }

    public void validate(Object target, Errors errors) {
        UserCreateForm form = (UserCreateForm) target;
        logger.info("Walidacja {}", target);
        if (form.getPassword() == null || form.getConfirmPassword() == null)
            errors.rejectValue("password", "password_error");
        else if (!form.getPassword().equals(form.getConfirmPassword())) {
            errors.rejectValue("password", "password_error");
        }

        if (UserService.getUserByEmail(form.getEmail()).isPresent()) {
            errors.rejectValue("email", "email_error");
        }
    }

    @Override
    public String toString() {
        return "UserCreateValidator{" +
                "UserServiceImpl=" + UserService +
                '}';
    }
}
