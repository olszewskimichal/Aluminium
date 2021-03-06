package com.zespolowka.config;

import javax.annotation.PostConstruct;
import java.text.ParseException;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Slf4j
public class DevDBConfig {

	private static final String SYSTEM = "SYSTEM";
	@Autowired
	private UserRepository repository;

	@PostConstruct
	public void populateDatabase() throws ParseException {
		log.info("ładowanie bazy testowej");
		User system = new User(SYSTEM, SYSTEM, SYSTEM, new BCryptPasswordEncoder().encode(SYSTEM));
		repository.save(system);
		User user = new User("Uzytkownik", "Ambitny", "aaa1@o2.pl", new BCryptPasswordEncoder().encode("aaa"));
		user.unblock();
		repository.save(user);
		user = new User("Admin", "admin", "aaa2@o2.pl", new BCryptPasswordEncoder().encode("1"));
		user.unblock();
		user.changeRole(Role.ADMIN);
		repository.save(user);
		user = new User("SuperAdmin", "superadmin", "aaa3@o2.pl", new BCryptPasswordEncoder().encode("a"));
		user.changeRole(Role.SUPERADMIN);
		user.unblock();
		repository.save(user);
	}
}
