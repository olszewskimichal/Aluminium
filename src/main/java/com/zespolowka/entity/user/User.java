package com.zespolowka.entity.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zespolowka.entity.solution.test.SolutionTest;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Created by Pitek on 2015-11-29.
 */
@Entity
@Table(name = "users")
@Getter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	private String lastName;

	private String email;

	@JsonIgnore
	private String passwordHash;

	@JsonIgnore
	private boolean enabled;

	@JsonIgnore
	@Setter
	private int loginTries;

	@JsonIgnore
	private boolean accountNonLocked = true;

	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private Role role;
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SolutionTest> solutionTests = new ArrayList<>();

	public User() {
		this.enabled = false;
		this.loginTries = 3;
	}


	public User(String name, String lastName, String email, String passwordHash) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = Role.USER;
		this.enabled = false;
		this.loginTries = 3;
	}

	public void changeRole(Role role) {
		this.role = role;
	}

	public void enable() {
		this.enabled = true;
	}

	public void disable() {
		this.enabled = false;
	}

	public void unblock() {
		this.accountNonLocked = true;
	}

	public void block() {
		this.accountNonLocked = false;
	}

	public void restartPassword(String randomHash) {
		passwordHash = new BCryptPasswordEncoder().encode(randomHash);
	}

	public void addSolution(SolutionTest solutionTest) {
		this.solutionTests.add(solutionTest);
		solutionTest.setUser(this);
	}

	public void removeSolution(SolutionTest solutionTest) {
		solutionTests.remove(solutionTest);
		solutionTest.setUser(null);
	}

}


