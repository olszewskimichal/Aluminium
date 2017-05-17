package com.zespolowka.service;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import com.zespolowka.entity.user.CurrentUser;
import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Admin on 2015-12-01.
 */
@Service
@Slf4j
public class UserService {
	private final UserRepository userRepository;

	private final NotificationService notificationService;

	private final VerificationTokenService verificationTokenService;

	@Autowired
	public UserService(UserRepository userRepository, NotificationService notificationService, VerificationTokenService verificationTokenService) {
		this.userRepository = userRepository;
		this.notificationService = notificationService;
		this.verificationTokenService = verificationTokenService;
	}

	public Optional<User> getUserById(long id) {
		log.info("Pobieranie uzytkownika o id = {}", id);
		return Optional.ofNullable(userRepository.findOne(id));
	}

	public Optional<User> getUserByEmail(String email) {
		log.info("Pobieranie uzytkownika o mailu = {}", email);
		return userRepository.findUserByEmail(email);
	}

	public Collection<User> getAllUsers() {
		log.info("Pobieranie wszystkich uzytkownikow");
		return (Collection<User>) userRepository.findAll();
	}

	public Collection<User> findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String like) {
		return userRepository.findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(like, like, like);
	}

	public User create(UserCreateForm form, String registerUrl) throws MessagingException {
		User user = new User();
		user.setName(form.getName());
		user.setLastName(form.getLastName());
		user.setEmail(form.getEmail());
		user.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
		user.setRole(form.getRole());
		log.info("Stworzono uzytkownika");
		//TODO naprawic te chujnie
		//String token = UUID.randomUUID().toString();
		//VerificationToken verificationToken = verificationTokenService.create(user, token);
		//String url = registerUrl + "/registrationConfirm?token=" + verificationToken.getToken();
		//mailService.sendVerificationMail(url, user);
		log.info(user.toString());
		return userRepository.save(user);
	}


	public User editUser(UserEditForm userEditForm) {
		User user = getUserById(userEditForm.getId()).orElseThrow(() -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", userEditForm.getId())));
		user.setName(userEditForm.getName());
		user.setLastName(userEditForm.getLastName());
		user.setEmail(userEditForm.getEmail());
		user.setRole(userEditForm.getRole());
		if (userEditForm.getPassword() == null)
			userEditForm.setPassword("");
		if (!userEditForm.getPassword().isEmpty()) {
			user.setPasswordHash(new BCryptPasswordEncoder().encode(userEditForm.getPassword()));
		}
		log.info("Edytowano uzytkownika");
		return userRepository.save(user);
	}

	public User update(User user) {
		return userRepository.save(user);
	}

	public Boolean deleteUserById(long userId) {
		User currentUser = getCurrentUser();
		User user = getUserById(userId).orElseThrow(() -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", userId)));
		if (Objects.equals(currentUser.getId(), userId) || "ADMIN".equals(currentUser.getRole().name()) && "SUPERADMIN".equals(user.getRole().name())) {
			return false;
		}
		notificationService.deleteMessagesBySender(user);
		notificationService.deleteMessagesByUserId(user.getId());
		verificationTokenService.deleteVerificationTokenByUser(userRepository.findOne(userId));
		userRepository.delete(userId);
		return true;
	}

	public User getCurrentUser() {
		CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return currentUser.getUser();
	}

	@Transactional
	public Pair<Boolean, String> changeUserActivity(Long userId) {
		User currentUser = getCurrentUser();
		User user = getUserById(userId).orElseThrow(() -> new NoSuchElementException(String.format("Uzytkownik o id =%s nie istnieje", userId)));
		if (currentUser.getRole().equals(Role.SUPERADMIN)) {
			user.setEnabled(!user.isEnabled());
			if (user.isEnabled())
				return new ImmutablePair<>(true, "Deaktywowano uzytkownika " + user.getEmail());
			else
				return new ImmutablePair<>(true, "Aktywowano uzytkownika " + user.getEmail());
		}
		return new ImmutablePair<>(false, "Nie masz uprawnień");
	}
}
