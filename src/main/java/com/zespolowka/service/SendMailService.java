package com.zespolowka.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;

import com.zespolowka.entity.user.User;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Pitek on 2016-02-17.
 */
@Service
@Slf4j
public class SendMailService {

	private final JavaMailSender mailSender;
	private final MimeMessage mimeMessage;
	private final UserService userservice;
	private MimeMessageHelper message;

	@Autowired
	public SendMailService(JavaMailSender mailSender, UserService userservice) throws MessagingException {
		this.mailSender = mailSender;
		this.userservice = userservice;
		mimeMessage = mailSender.createMimeMessage();
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
	}


	public void sendVerificationMail(String url, User user) throws MessagingException {
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setFrom("noreply@gmail.com");
		message.setTo(user.getEmail());
		message.setSubject("Confirm Registration");
		message.setText("<html><body>" + "<h4>Witaj, " + user.getName() + ' ' + user.getLastName() + "</h4>" + "<i>" + "<a href=" + url + ">" + "<strong>" + "kliknij tutaj by aktywować swoje konto!" + "</strong>" + "</a>" + "</i>" + "</body></html>", true);
		mailSender.send(mimeMessage);

	}

	public void sendReminderMail(User user) throws MessagingException {
		String letters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(8);
		for (int i = 0; i < 8; i++) {
			sb.append(letters.charAt(rnd.nextInt(letters.length())));
		}
		message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
		message.setFrom("noreply@gmail.com");
		message.setTo(user.getEmail());
		message.setSubject("Przypomnienie Hasła");
		String newPassword = sb.toString();
		user.setPasswordHash(new BCryptPasswordEncoder().encode(newPassword));
		userservice.update(user);
		message.setText("<html><body><h4>Witaj " + user.getName() + "!</h4><p>Twoje nowe hasło to: " + newPassword + "</p></body></html>", true);
		mailSender.send(mimeMessage);
		log.info("Reminder sent", newPassword);

	}

}
