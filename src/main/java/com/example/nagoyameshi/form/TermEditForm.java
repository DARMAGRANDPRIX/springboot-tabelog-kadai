package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TermEditForm {
    @NotNull
    private Integer id;     
    
    @NotBlank(message = "内容を入力してください。")
    private String content;
}
