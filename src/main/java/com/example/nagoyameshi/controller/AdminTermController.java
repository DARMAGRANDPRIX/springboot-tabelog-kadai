package com.example.nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Term;
import com.example.nagoyameshi.form.TermEditForm;
import com.example.nagoyameshi.repository.TermRepository;
import com.example.nagoyameshi.service.TermService;

@Controller
@RequestMapping("/admin/terms")
public class AdminTermController {
    private final TermRepository termRepository;   
    private final TermService termService;       
    
    public AdminTermController(TermRepository termRepository, TermService termService) {
        this.termRepository = termRepository;         
        this.termService = termService;         
    }
    
    @GetMapping
    public String index(Model model) {         
        Term term = termRepository.findFirstByOrderByIdAsc();  
        
        model.addAttribute("term", term);
        
        return "admin/terms/index";
    }
    
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable(name = "id") Integer id, Model model) {        
        Term term= termRepository.getReferenceById(id);  
        TermEditForm termEditForm = new TermEditForm(term.getId(), term.getContent());        
        model.addAttribute("termEditForm", termEditForm);
        
        return "admin/terms/edit";
    }   
    
    @PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated TermEditForm termEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
        if (bindingResult.hasErrors()) {
            return "admin/terms/edit";
        }
        
        termService.update(termEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "利用規約を編集しました。");
        
        return "redirect:/admin/terms";
    }
}
