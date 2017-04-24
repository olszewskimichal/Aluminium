package com.zespolowka.service;

import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.forms.UserEditForm;
import com.zespolowka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Admin on 2015-12-01.
 */
@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    private final VerificationTokenService verificationTokenService;

    @Autowired
    public UserService(UserRepository userRepository, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
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
        return userRepository.findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(
                like, like, like);
    }

    public User create(UserCreateForm form) {
        User user = new User();
        user.setName(form.getName());
        user.setLastName(form.getLastName());
        user.setEmail(form.getEmail());
        user.setPasswordHash(new BCryptPasswordEncoder().encode(form.getPassword()));
        user.setRole(form.getRole());
        log.info("Stworzono uzytkownika");
        return userRepository.save(user);
    }


    public User editUser(UserEditForm userEditForm) {
        User user = getUserById(userEditForm.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Uzytkownik o id =%s nie istnieje", userEditForm.getId())));
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

    public void delete(long id) {
        verificationTokenService.deleteVerificationTokenByUser(userRepository.findOne(id));
        userRepository.delete(id);
    }

}
