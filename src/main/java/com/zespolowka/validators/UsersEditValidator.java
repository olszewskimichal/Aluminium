package com.zespolowka.validators;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.NoSuchElementException;


@Component
public class UsersEditValidator implements Validator {
    private static final Logger logger = LoggerFactory.getLogger(UserCreateValidator.class);

    private final UserService userService;

    @Autowired
    public UsersEditValidator(UserService userService) {
        this.userService = userService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(UserEditForm.class);
    }

    public void validate(Object target, Errors errors) {
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = currentUser.getUser();
        UserEditForm form = (UserEditForm) target;
        logger.info("Walidacja edycji{}", target);

        User usr1 = userService.getUserByEmail(form.getEmail()).orElse(new User());
        User usr2 = userService.getUserById(form.getId()).orElse(new User());
        if (!usr1.equals(usr2)) {
            if (userService.getUserByEmail(form.getEmail()).isPresent()) {
                errors.rejectValue("email", "email_error");
            }
        }
        try {
            User tempUser = userService.getUserById(form.getId()).get();
            if (!user.getRole().equals(Role.SUPERADMIN) && !(tempUser.getRole().equals(form.getRole()))) {
                errors.rejectValue("role", "permission_denied");
            }

            if (!user.getRole().equals(Role.SUPERADMIN) && tempUser.getRole().equals(Role.SUPERADMIN)) {
                errors.rejectValue("role", "permission_denied");
            }
        } catch (NoSuchElementException e) {
            errors.rejectValue("id", "id_error2");
        }
    }
}
