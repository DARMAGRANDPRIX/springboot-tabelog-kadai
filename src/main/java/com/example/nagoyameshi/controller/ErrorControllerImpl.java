package com.example.nagoyameshi.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ErrorControllerImpl implements ErrorController {
    @GetMapping("/error")
    public String index(HttpServletResponse httpServletResponse, Model model) {        
        if (httpServletResponse.getStatus() == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute("status", "404 Not Found");
            model.addAttribute("message", "ページが見つかりません。");
        }
        else if (httpServletResponse.getStatus() == HttpStatus.FORBIDDEN.value()) {
            model.addAttribute("status", "403 Forbidden");
            model.addAttribute("message", "アクセスしようとしたページは表示できませんでした。");
        }
        else if (httpServletResponse.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            model.addAttribute("status", "500 Internal Server Error");
            model.addAttribute("message", "アクセスしようとしたページは表示できませんでした。");
        }
        else {
            model.addAttribute("status", "Error");
            model.addAttribute("message", "エラーが発生しました。");
        }
        
        return "error";
    }
}
