package com.example.library.api.model.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {

    private Long id;

    @NotEmpty(message = "É necessário informar um título.")
    private String title;

    @NotEmpty(message = "É necessário informar um autor.")
    private String author;

    @NotEmpty(message = "É necessário informar um isbn.")
    private String isbn;
}
