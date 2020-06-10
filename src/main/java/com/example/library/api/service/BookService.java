package com.example.library.api.service;

import com.example.library.api.model.entity.Book;
import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
