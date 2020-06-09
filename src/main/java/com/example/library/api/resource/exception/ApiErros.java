package com.example.library.api.resource.exception;

import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.BindingResult;

public class ApiErros {

    private List<String> erros = new ArrayList<>();

    public ApiErros(BindingResult bindingResult) {
        bindingResult.getAllErrors().forEach(error -> erros.add(error.getDefaultMessage()));
    }

    public List<String> getErros() {
        return erros;
    }

}
