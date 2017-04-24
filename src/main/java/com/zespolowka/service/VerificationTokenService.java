package com.zespolowka.service;

import com.zespolowka.entity.VerificationToken;
import com.zespolowka.entity.user.User;
import com.zespolowka.repository.VerificationTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Pitek on 2016-02-17.
 */
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

    @Override
    public String toString() {
        return "VerificationTokenServiceImpl{" +
                "verificationTokenRepository=" + verificationTokenRepository +
                '}';
    }
}
