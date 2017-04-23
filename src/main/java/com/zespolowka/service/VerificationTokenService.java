package com.zespolowka.service;

import com.zespolowka.entity.VerificationToken;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.VerificationTokenRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Pitek on 2016-02-17.
 */
@Service
public class VerificationTokenService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(VerificationTokenService.class);

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public Optional<VerificationToken> getVerificationTokenByToken(String token) {
        logger.info("Pobieranie Tokena o tokenie = {}", token);
        return verificationTokenRepository.findVerificationTokenByToken(token);
    }

    public VerificationToken create(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user);
        return verificationTokenRepository.save(verificationToken);
    }

    public void deleteVerificationTokenByUser(User user) {
        logger.info("deleteVerificationTokenByUser");
        verificationTokenRepository.deleteVerificationTokenByUser(user);
    }

    @Override
    public String toString() {
        return "VerificationTokenServiceImpl{" +
                "verificationTokenRepository=" + verificationTokenRepository +
                '}';
    }
}
