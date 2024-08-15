package com.example.nagoyameshi.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.service.PasswordService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PasswordController {
	private final PasswordService passwordService;

	public PasswordController(PasswordService passwordService) {
		this.passwordService = passwordService;
	}

	@GetMapping("/email")
	public String email() {
		return "auth/email";
	}

	@PostMapping("/sendResetLink")
	public String sendEmail(@RequestParam("email") String email, Model model, HttpServletRequest request) {
		try {
			String url = request.getRequestURL().toString();
			String baseUrl = url.replace(request.getServletPath(), "");
			System.out.print(baseUrl);
			passwordService.sendResetEmail(email, baseUrl);
			model.addAttribute("message", "パスワードリセットリンクをメールで送信しました。");

		} catch (MessagingException | UnsupportedEncodingException e) {
			model.addAttribute("error", "メールの送信中にエラーが発生しました。もう一度お試しください。");
		}
		return "auth/emailSent";
	}

}
