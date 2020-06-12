package com.example.library.api.resource;

import com.example.library.api.model.dto.BookDTO;
import com.example.library.api.model.entity.Book;
import com.example.library.api.resource.exception.ResourceNofFoundException;
import com.example.library.api.service.BookService;
import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "/api/books")
public class BookController {

    @Autowired
    BookService service;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO dto) {

        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(entity.getId()).toUri();

        return ResponseEntity.created(uri).body(modelMapper.map(entity, BookDTO.class));
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDTO> get(@PathVariable Long id) {

        Book entity = existsBook(id);

        return ResponseEntity.ok().body(modelMapper.map(entity, BookDTO.class));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        Book entity = existsBook(id);

        service.delete(entity);
    }

    @PutMapping("{id}")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {

        Book entity = existsBook(id);

        entity.setAuthor(dto.getAuthor());
        entity.setTitle(dto.getTitle());

        return modelMapper.map(entity, BookDTO.class);
    }

    private Book existsBook(Long id) {
        Optional<Book> entity = service.getById(id);

        if (!entity.isPresent()) {
            throw new ResourceNofFoundException("Livro não encontrado.");
        }

        return entity.get();
    }

}
