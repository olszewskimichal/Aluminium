package com.zespolowka.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Pitek on 2015-11-29.
 */
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findUserByEmail(String email);

	List<User> findUsersByRole(Role role);

	Collection<User> findUsersByEmailIgnoreCaseContainingOrNameIgnoreCaseContainingOrLastNameIgnoreCaseContaining(String email, String name, String lastname);

	@Transactional
	@Modifying
	@Query("update User p set p.name = ?1, p.lastName = ?2, p.email = ?3,p.role=?4,p.passwordHash=?5 where p.id = ?6")
	User updateUser(String name, String lastName, String email, Role role, String pass, Long id);
}
