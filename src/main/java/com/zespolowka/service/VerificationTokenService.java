package com.zespolowka.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import com.zespolowka.entity.VerificationToken;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class VerificationTokenService {

	private final VerificationTokenRepository verificationTokenRepository;

	@Autowired
	public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
		this.verificationTokenRepository = verificationTokenRepository;
	}

	public Optional<VerificationToken> getVerificationTokenByToken(String token) {
		log.info("Pobieranie Tokena o tokenie = {}", token);
		return verificationTokenRepository.findVerificationTokenByToken(token);
	}

	public VerificationToken create(User user, String token) {
		VerificationToken verificationToken = new VerificationToken(token, user);
		return verificationTokenRepository.save(verificationToken);
	}

	public void deleteVerificationTokenByUser(User user) {
		log.info("deleteVerificationTokenByUser");
		verificationTokenRepository.deleteVerificationTokenByUser(user);
	}

	@Transactional
	public String confirmRegistrationToken(String token) {
		Optional<VerificationToken> verificationToken = getVerificationTokenByToken(token);
		if (verificationToken.isPresent()) {
			User user = verificationToken.get().getUser();
			LocalDateTime localDateTime = LocalDateTime.now();
			long diff = Duration.between(localDateTime, verificationToken.get().getExpiryDate()).toMinutes();

			if (diff < 0L) {
				log.info(String.format("Token juz jest nieaktulany %n dataDO= %s %n", verificationToken.get().getExpiryDate()));
				return "nieaktualny";
			}
			else {
				log.info("Token jest aktualny - aktywacja konta");
				user.enable();
				deleteVerificationTokenByUser(user);
				return "aktualny";
			}
		}
		return "blednyToken";
	}
}
