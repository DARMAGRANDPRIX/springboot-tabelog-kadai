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

	// パスワードリセットページへの遷移
	@GetMapping("/email")
	public String email() {
		return "auth/email";
	}

	// パスワードリセットリンク送信処理
	@PostMapping("/sendResetLink")
	public String sendEmail(@RequestParam("email") String email, Model model, HttpServletRequest request) {
		try {
			String url = request.getRequestURL().toString();
			String baseUrl = url.replace(request.getServletPath(), "");
			passwordService.sendResetEmail(email, baseUrl);
			model.addAttribute("message", "パスワードリセットリンクをメールで送信しました。");

		} catch (MessagingException | UnsupportedEncodingException e) {
			model.addAttribute("error", "メールの送信中にエラーが発生しました。もう一度お試しください。");
		}
		return "auth/emailSent";
	}

	// パスワードリセットリンククリック時の処理
	@GetMapping("/updatePassword")
	public String updatePassword(@RequestParam("token") String token, Model model) {
		if (passwordService.isValidToken(token)) {
			model.addAttribute("token", token);
			return "auth/updatePassword";
		} else {
			model.addAttribute("error", "無効なトークンです。もう一度お試しください。");
			return "auth/emailSent";
		}
	}

	// パスワード再設定処理
	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam("token") String token, @RequestParam("password") String password,
			Model model) {
		if (passwordService.updatePassword(token, password)) {
			model.addAttribute("message", "パスワードが正常にリセットされました。");
			return "redirect:/login";
		} else {
			model.addAttribute("error", "パスワードリセット中にエラーが発生しました。");
			return "auth/updatePassword";
		}
	}

}
