package com.example.library.api.resource.exception;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> notValidFields(MethodArgumentNotValidException exception) {
        Set<ErrorsInFields> errors = new HashSet<>();

        BindingResult bindingResult = exception.getBindingResult();

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        fieldErrors.forEach(field -> {
            ErrorsInFields err = new ErrorsInFields();
            err.setField(field.getField());
            List<FieldError> errorsToField = bindingResult.getFieldErrors(field.getField());
            errorsToField.forEach(error -> err.getErros().add(error.getDefaultMessage()));
            errors.add(err);
        });

        return ResponseEntity.badRequest().body(new Errors(errors));
    }

    @ExceptionHandler(ResourceNofFoundException.class)
    public ResponseEntity<Error> resourceNotFound(ResourceNofFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(exception.getMessage()));
    }
}
