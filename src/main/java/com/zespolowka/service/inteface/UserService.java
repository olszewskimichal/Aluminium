package com.zespolowka.service.inteface;

import com.zespolowka.entity.user.User;
import com.zespolowka.forms.UserCreateForm;
import com.zespolowka.forms.UserEditForm;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Admin on 2015-12-01.
 */
public interface UserService {

    Optional<User> getUserById(long id);

    Optional<User> getUserByEmail(String email);

    Collection<User> getAllUsers();

    Collection<User> findByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String like);

    User create(UserCreateForm form);

    User editUser(UserEditForm userEditForm);

    User update(User user);
}