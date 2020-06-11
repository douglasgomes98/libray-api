package com.example.library.api.service;

import com.example.library.api.model.entity.Book;
import com.example.library.api.repository.BookRepository;
import com.example.library.api.service.impl.BookServiceImpl;
import com.example.library.exception.BusinessException;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBook() {

        Book book = createNewBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(repository.save(book)).thenReturn(Book.builder()
                .id(11L)
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build());

        Book savedBook = service.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado.")
    public void shouldNotSaveABookWithDuplicatedIsbn() {

        Book book = createNewBook();

        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        Assertions.assertThat(exception).isInstanceOf(BusinessException.class);
        Assertions.assertThat(exception).hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por Id.")
    public void getBookById() {

        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        Assertions.assertThat(foundBook.isPresent());
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

    }

    private Book createNewBook() {
        return Book.builder()
                .author("Fulano")
                .title("As aventuras")
                .isbn("123")
                .build();
    }

}
