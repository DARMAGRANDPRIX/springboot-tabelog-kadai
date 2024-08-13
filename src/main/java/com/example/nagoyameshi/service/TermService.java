package com.example.nagoyameshi.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Term;
import com.example.nagoyameshi.form.TermEditForm;
import com.example.nagoyameshi.repository.TermRepository;

@Service
public class TermService {
    private final TermRepository termRepository;         
    
    public TermService(TermRepository termRepository) {        
        this.termRepository = termRepository;         
    }
    
    @Transactional
    public void update(TermEditForm termEditForm) {        
        Term term = termRepository.getReferenceById(termEditForm.getId());
        
        term.setContent(termEditForm.getContent());
        
        termRepository.save(term);
    }
}
