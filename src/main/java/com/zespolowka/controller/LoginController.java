package com.zespolowka.controller;


import com.zespolowka.entity.user.User;
import com.zespolowka.service.SendMailService;
import com.zespolowka.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Controller
@Slf4j
public class LoginController {
    private final UserService userService;
    private final SendMailService sendMailService;

    @Autowired
    public LoginController(UserService userService, SendMailService sendMailService) {
        this.userService = userService;
        this.sendMailService = sendMailService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage(Model model, @RequestParam Optional<String> error) {
        log.info("nazwa metody = getLoginPage");
        model.addAttribute("error", error);
        return "login";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model, HttpServletRequest request) {
        log.info("nazwa metody = loginError");
        model.addAttribute("loginError", true);
        model.addAttribute("errorMessage", request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
        return "login";
    }

    @RequestMapping("/login-expired")
    public String loginExpired(Model model) {
        log.info("nazwa metody = loginError");
        model.addAttribute("loginExpired", true);
        return "login";
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
        try {
            User user = userService.getUserByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            String.format("Uzytkownik z mailem=%s nie istnieje", email)));
            sendMailService.sendReminderMail(user);
            log.info("Email:", email);
            model.addAttribute("sukces", true);
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return "remindPassword";
    }

}
