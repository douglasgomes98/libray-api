package com.example.library.api.resource.exception;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Errors {

    private Set<ErrorsInFields> errors = new HashSet<>();
}
