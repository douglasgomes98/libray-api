package com.example.library.api.service.impl;

import com.example.library.api.model.entity.Book;
import com.example.library.api.repository.BookRepository;
import com.example.library.api.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {;
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }

}
