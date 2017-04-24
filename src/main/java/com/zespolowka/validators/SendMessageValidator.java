package com.zespolowka.validators;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class SendMessageValidator implements Validator {
    private final UserService UserService;

    @Autowired
    public SendMessageValidator(UserService UserService) {
        this.UserService = UserService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(NewMessageForm.class);
    }

    public void validate(Object target, Errors errors) {
        NewMessageForm form = (NewMessageForm) target;
        log.info("Walidacja adresatow {}", ((NewMessageForm) target).getReceivers());
        if (form.getReceivers().isEmpty()) {
            errors.rejectValue("receivers", "notification.receivers_empty");
        } else {
            String receivers;
            if (form.getReceivers().endsWith(", "))
                receivers = form.getReceivers().substring(0, form.getReceivers().length() - 2);
            else receivers = form.getReceivers();

            String result[] = receivers.split(",");
            for (String s : result) {
                String st = s.replaceAll("\\s+", "");
                if (s.contains("@")) {
                    Optional<User> usr = UserService.getUserByEmail(st);
                    if (!usr.isPresent()) {
                        errors.rejectValue("receivers", "notification.receiver_invalid");
                    }
                } else {
                    String st2 = st.toUpperCase();
                    if (!Arrays.asList(Role.ADMIN.name(), Role.SUPERADMIN.name(), Role.USER.name()).contains(st2)) {
                        errors.rejectValue("receivers", "notification.role_invalid");
                    }
                }
            }
        }
        if (form.getMessage().length() > 10000) {
            errors.rejectValue("message", "notification.message_tooLong");
        }
        if (form.getTopic().length() > 254) {
            errors.rejectValue("topic", "notification.topic_tooLong");
        }
    }

    @Override
    public String toString() {
        return "SendMessageValidator{" +
                "UserServiceImpl=" + UserService +
                '}';
    }
}