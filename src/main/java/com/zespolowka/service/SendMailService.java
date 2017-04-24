package com.zespolowka.service;

import com.zespolowka.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;

/**
 * Created by Pitek on 2016-02-17.
 */
@Service
@Slf4j
public class SendMailService {

    private final JavaMailSender mailSender;
    private final MimeMessage mimeMessage;
    private final UserService UserService;
    private MimeMessageHelper message;

    @Autowired
    public SendMailService(JavaMailSender mailSender, UserService UserService) {
        this.mailSender = mailSender;
        this.UserService = UserService;
        mimeMessage = mailSender.createMimeMessage();
    }


    public void sendVerificationMail(String url, User user) {
        try {
            message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom("noreply@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Confirm Registration");
            message.setText("<html><body>" +
                    "<h4>Witaj, " + user.getName() + ' ' + user.getLastName() + "</h4>" +
                    "<i>" + "<a href=" + url + ">" + "<strong>" + "kliknij tutaj by aktywować swoje konto!" + "</strong>" + "</a>" + "</i>" +
                    "</body></html>", true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendReminderMail(User user) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        try {
            message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom("noreply@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Przypomnienie Hasła");
            String newPassword = sb.toString();
            user.setPasswordHash(new BCryptPasswordEncoder().encode(newPassword));
            UserService.update(user);
            message.setText(
                    "<html><body><h4>Witaj " + user.getName() + "!</h4><p>Twoje nowe hasło to: " + newPassword + "</p></body></html>",
                    true);
            mailSender.send(mimeMessage);
            log.info("Reminder sent", newPassword);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
