package com.zespolowka.controller.rest;

import com.zespolowka.entity.Notification;
import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.User;
import com.zespolowka.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Slf4j
public class NotificationRestController {
    private static final int INITIAL_PAGE = 0;
    private static final int MESSAGES_ON_PAGE = 6;
    private final NotificationService notificationService;
    private CurrentUser currentUser;
    private User user;

    @Autowired
    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Notification> getNotifications(@RequestParam(value = "page", required = false) Integer page) {
        int evalPage = (page == null || page < 1) ? INITIAL_PAGE : page - 1;
        currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = currentUser.getUser();
        return notificationService.findAllPageable(new PageRequest(evalPage, MESSAGES_ON_PAGE), user.getId(),
                user.getRole()).getContent();
    }

    @CrossOrigin
    @RequestMapping(value = "/top5", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Notification> getTop5Notifications(@RequestParam(value = "page", required = false) Integer page) {
        int evalPage = (page == null || page < 1) ? INITIAL_PAGE : page - 1;
        currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = currentUser.getUser();
        return notificationService.findAllPageable(new PageRequest(evalPage, 5), user.getId(), user.getRole())
                .getContent();
    }

    @CrossOrigin
    @RequestMapping(value = "/get/totalPages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Integer getTotalPages(@RequestParam(value = "page", required = false) Integer page) {
        int evalPage = (page == null || page < 1) ? INITIAL_PAGE : page - 1;
        currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = currentUser.getUser();
        return notificationService.findAllPageable(new PageRequest(evalPage, MESSAGES_ON_PAGE), user.getId(),
                user.getRole()).getTotalPages();
    }

    @CrossOrigin
    @RequestMapping(value = "/get/msgCount", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getMsgCount() {
        currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = currentUser.getUser();
        long unreadNotificationById = notificationService.countByUnreadAndUserId(true, user.getId());
        long unreadNotificationByRole = notificationService.countByUnreadAndUserRole(true, user.getRole());
        return unreadNotificationById + unreadNotificationByRole;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, value = "/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNotification(@PathVariable("id") Long id, @RequestBody Notification notification) {
        log.info("Zaktualizuj wiadomosc o id {}", id);
        notificationService.changeStatus(id);
    }

    @CrossOrigin
    @RequestMapping(value = "/updateStatus", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void changeQuantity(@RequestParam(value = "id", required = true) Long id) {
        log.info("Zaktualizuj wiadomosc o id {}", id);
        notificationService.changeStatus(id);
    }
}
