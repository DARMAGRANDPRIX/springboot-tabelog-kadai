package com.example.nagoyameshi.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private final JavaMailSender javaMailSender;

	public EmailService(JavaMailSender mailSender) {
		this.javaMailSender = mailSender;
	}

	public void sendResetEmail(String email, String link) {
		String recipientAddress = email;
		String subject = "パスワードリセットリンク";
		String resetLink = link;
		String message = "以下のリンクをクリックしてパスワードをリセットしてください。";
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + resetLink);
		javaMailSender.send(mailMessage);
	}
}
