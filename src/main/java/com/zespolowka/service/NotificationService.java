package com.zespolowka.service;

import com.zespolowka.entity.Notification;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.NewMessageForm;
import com.zespolowka.repository.NotificationRepository;
import com.zespolowka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;


@Service
@Transactional
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    public Notification getNotificationById(Long id) {
        log.info("getNotificationById = {}", id);
        return notificationRepository.findOne(id);
    }


    public Collection<Notification> findTop5ByUserIdOrUserRoleOrderByDateDesc(Long userId, Role userRole) {
        log.info("findTop5ByUserId={}OrUserRole={}OrderByDateDesc = ", userId, userRole);
        return notificationRepository.findTop5ByUserIdOrUserRoleOrderByDateDesc(userId, userRole);
    }

    public Long countByUnreadAndUserId(boolean unread, Long userId) {
        log.info("countByUnread={}AndUserId={}", unread, userId);
        return notificationRepository.countByUnreadAndUserId(unread, userId);
    }

    public Long countByUnreadAndUserRole(boolean unread, Role userRole) {
        log.info("countByUnread={}AndUserId={}", unread, userRole);
        return notificationRepository.countByUnreadAndUserRole(unread, userRole);
    }

    public Notification createNotification(Notification notification) {
        log.info("createNotif{}", notification);
        return notificationRepository.save(notification);
    }

    public Page<Notification> findAllPageable(Pageable pageable, Long userId, Role userRol) {
        return notificationRepository.findAllByUserIdOrUserRoleOrderByDateDesc(pageable, userId, userRol);
    }

    public Notification changeStatus(Long idNotification) {
        Notification notification = notificationRepository.getOne(idNotification);
        notification.setUnread(false);
        return notificationRepository.save(notification);
    }

    public void sendMessage(NewMessageForm form) {
        try {
            String receivers;
            if (form.getReceivers().endsWith(", "))
                receivers = form.getReceivers().substring(0, form.getReceivers().length() - 2);
            else receivers = form.getReceivers();

            String result[] = receivers.split(",");
            Notification notif;
            ArrayList<String> wyslane = new ArrayList<>();
            for (String s : result) {

                String st = s.replaceAll("\\s+", "");
                if (wyslane.contains(st)) {
                    continue;
                }
                if (st.contains("@")) {
                    User usr = userRepository.findUserByEmail(st)
                            .orElseThrow(() -> new NoSuchElementException(
                                    String.format("Uzytkownik o emailu =%s nie istnieje", st)));
                    notif = new Notification(form.getMessage(), form.getTopic(), usr.getId(), form.getSender());
                    log.info("Wiadomosc wyslana do: {}", st);
                    notificationRepository.save(notif);
                    wyslane.add(st);
                } else {
                    String st2 = st.toUpperCase();
                    if (st2.equals(Role.ADMIN.name()) || st2.equals(Role.SUPERADMIN.name()) || st2.equals(
                            Role.USER.name())) {
                        Collection<User> users = userRepository.findUsersByRole(Role.valueOf(st2));
                        for (User usr : users) {
                            notif = new Notification(form.getMessage(), form.getTopic(), usr.getId(), form.getSender());
                            log.info("Grupowa wiadomosc do: {}", usr.getEmail());
                            notificationRepository.save(notif);
                            wyslane.add(st);
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            log.info(form.toString());
        }
    }

    public void deleteMessagesByUserId(Long id) {
        log.info("deleteMessagesByUserId");
        log.info("Przed usunieciem:{}", notificationRepository.count());
        notificationRepository.deleteByUserId(id);
        log.info("Po usunieciu:{}", notificationRepository.count());
    }

    public void deleteMessagesBySender(User user) {
        notificationRepository.deleteMessagesBySender(user);
    }
}



