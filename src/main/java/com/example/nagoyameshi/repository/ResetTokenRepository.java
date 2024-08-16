package com.example.nagoyameshi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.ResetToken;
import com.example.nagoyameshi.entity.User;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Integer> {
	public ResetToken findByToken(String token);

	public ResetToken findByUser(User user);
}
