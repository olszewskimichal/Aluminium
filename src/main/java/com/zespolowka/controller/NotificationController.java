package com.zespolowka.controller;

import com.zespolowka.entity.Notification;
import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.service.NotificationService;
import com.zespolowka.validators.SendMessageValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    private final SendMessageValidator sendMessageValidator;

    @Autowired
    public NotificationController(NotificationService notificationService, SendMessageValidator sendMessageValidator) {
        this.notificationService = notificationService;
        this.sendMessageValidator = sendMessageValidator;
    }


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String showNotifications(final Model model, @ModelAttribute("Notification") final Notification notification) {
        logger.info("nazwa metody = showNotifications");
        try {
            model.addAttribute("idNotification", notification.getId());
        } catch (final RuntimeException e) {
            logger.error(e.getMessage(), e);
        }
        return "messages";
    }

    @RequestMapping(value = "/messages/{id}", method = RequestMethod.GET)
    public String readNotification(@PathVariable final Integer id, final RedirectAttributes redirectAttributes) {
        logger.info("nazwa metody = readNotification");
        Notification notif = notificationService.getNotificationById(id.longValue());
        notif.setUnread(false);
        notificationService.createNotification(notif);
        redirectAttributes.addFlashAttribute("Notification", notif);
        return "redirect:/messages";
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
    public String newMessage(final Model model) {
        logger.info("nazwa metody = newMessage");
        model.addAttribute("newMessageForm", new NewMessageForm());
        return "sendMessage";
    }

    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public String sendMessage(final Model model, @ModelAttribute final NewMessageForm newMessageForm, BindingResult errors) {
        logger.info("nazwa metody = sendMessage");
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Curr:{}", currentUser.getUser());
        newMessageForm.setSender(currentUser.getUser());
        sendMessageValidator.validate(newMessageForm, errors);
        if (errors.hasErrors()) {
            String err = errors.getAllErrors().get(0).toString();
            logger.info("err:{}", err);
            try {
                notificationService.sendMessage(newMessageForm);
            } catch (final RuntimeException e) {
                logger.info("\n{}" + '\n', model);
            }
            return "sendMessage";
        } else {
            notificationService.sendMessage(newMessageForm);
            logger.info("Przeszly maile");
            model.addAttribute("sukces", true);
            model.addAttribute("newMessageForm", new NewMessageForm());
            return "sendMessage";
        }
    }
}