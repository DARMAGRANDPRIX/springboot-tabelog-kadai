package com.example.nagoyameshi.service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.ResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.ResetTokenRepository;
import com.example.nagoyameshi.repository.UserRepository;

import jakarta.mail.MessagingException;

@Service
public class PasswordService {
	private final UserRepository userRepository;
	private final EmailService emailService;
	private final ResetTokenRepository resetTokenRepository;
	private final PasswordEncoder passwordEncoder;

	public PasswordService(UserRepository userRepository, EmailService emailService,
			ResetTokenRepository resetTokenRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.emailService = emailService;
		this.resetTokenRepository = resetTokenRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void sendResetEmail(String email, String baseUrl) throws MessagingException, UnsupportedEncodingException {
		User user = userRepository.findByEmail(email);
		ResetToken resetToken = new ResetToken();
		if (user != null) {
			// 既存のトークンを削除または更新
			ResetToken existingToken = resetTokenRepository.findByUser(user);
			if (existingToken != null) {
				resetTokenRepository.delete(existingToken);
			}
			String token = UUID.randomUUID().toString();
			resetToken.setToken(token);
			resetToken.setUser(user);
			resetTokenRepository.save(resetToken);

			String resetLink = baseUrl + "/updatePassword?token=" + token;
			emailService.sendResetEmail(email, resetLink);
		}
	}

	public boolean isValidToken(String Token) {
		ResetToken resetToken = resetTokenRepository.findByToken(Token);

		return resetToken != null;
	}

	// パスワード再設定処理
	public boolean updatePassword(String token, String newPassword) {
		User user = resetTokenRepository.findByToken(token).getUser();
		if (user != null) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			return true;
		}
		return false;
	}

}