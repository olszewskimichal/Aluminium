package com.zespolowka.config;

import javax.annotation.PostConstruct;
import java.text.ParseException;

import com.zespolowka.entity.user.Role;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Profile("!prod")
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
		user.setEnabled(true);
		repository.save(user);
		user = new User("Admin", "admin", "aaa2@o2.pl", new BCryptPasswordEncoder().encode("1"));
		user.setEnabled(true);
		user.setRole(Role.ADMIN);
		repository.save(user);
		user = new User("SuperAdmin", "superadmin", "aaa3@o2.pl", new BCryptPasswordEncoder().encode("a"));
		user.setRole(Role.SUPERADMIN);
		user.setEnabled(true);
		repository.save(user);
	}
}
