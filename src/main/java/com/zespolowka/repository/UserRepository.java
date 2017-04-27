package com.zespolowka.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by Pitek on 2015-11-29.
 */
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findUserByEmail(String email);

	List<User> findUsersByRole(Role role);

	Collection<User> findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String email, String name, String lastname);

}
