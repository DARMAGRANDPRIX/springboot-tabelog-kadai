package com.example.nagoyameshi.service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.ResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class PasswordService {
	private final UserRepository userRepository;
	private final EmailService emailService;

	public PasswordService(UserRepository userRepository, EmailService emailService) {
		this.userRepository = userRepository;
		this.emailService = emailService;
	}

	public void sendResetEmail(String email, String baseUrl) throws MessagingException, UnsupportedEncodingException {
		User user = userRepository.findByEmail(email);
		ResetToken resetToken = new ResetToken();
		if (user != null) {
			String token = UUID.randomUUID().toString();
			resetToken.setToken(token);
			resetToken.setUser(user);

			String resetLink = baseUrl + "/passwprdReset?token=" + token;
			emailService.sendResetEmail(email, resetLink);
		}
	}
}