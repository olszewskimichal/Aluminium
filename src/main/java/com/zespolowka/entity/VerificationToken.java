package com.zespolowka.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import com.zespolowka.entity.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

	//liczba dni
	private static final int EXPIRATION = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String token;

	@OneToOne(targetEntity = User.class, optional = false)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	private LocalDateTime expiryDate;

	public VerificationToken(String token, User user) {
		super();
		this.token = token;
		this.user = user;
		this.expiryDate = calculateExpiryDate(EXPIRATION);
	}

	private LocalDateTime calculateExpiryDate(int expiryTime) {
		LocalDateTime now = LocalDateTime.now();
		return now.plusDays(expiryTime);
	}

}
