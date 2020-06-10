package com.example.library.api.resource.exception;

import com.example.library.exception.BusinessException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.validation.BindingResult;

public class ApiErros {

    private List<String> errors = new ArrayList<>();

    public ApiErros(BindingResult bindingResult) {
        bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
    }

    public ApiErros(BusinessException exception) {
        errors = Arrays.asList(exception.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }

}
