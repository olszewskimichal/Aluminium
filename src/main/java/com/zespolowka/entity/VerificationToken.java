package com.zespolowka.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import com.zespolowka.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class VerificationToken {

	//liczba dni
	private static final int EXPIRATION = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String token;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	private User user;

	private LocalDateTime expiryDate;

	public VerificationToken(String token, User user) {
		super(); this.token = token; this.user = user;
		this.expiryDate = calculateExpiryDate(EXPIRATION);
	}

	private LocalDateTime calculateExpiryDate(int expiryTime) {
		LocalDateTime now = LocalDateTime.now();
		return now.plusDays(expiryTime);
	}

}
