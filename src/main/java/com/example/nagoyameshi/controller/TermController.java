package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.nagoyameshi.entity.Term;
import com.example.nagoyameshi.repository.TermRepository;

@Controller
@RequestMapping("/terms")
public class TermController {
    private final TermRepository termRepository;           
    
    public TermController(TermRepository termRepository) {
        this.termRepository = termRepository;                 
    }
    
    @GetMapping
    public String index(Model model) {         
        Term term = termRepository.findFirstByOrderByIdAsc();  
        
        model.addAttribute("term", term);
        
        return "terms/index";
    }    
}
