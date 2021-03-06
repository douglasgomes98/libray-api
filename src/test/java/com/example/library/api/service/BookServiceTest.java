package com.example.library.api.service;

import com.example.library.api.model.entity.Book;
import com.example.library.api.repository.BookRepository;
import com.example.library.api.service.exception.IsbnDuplicatedException;
import com.example.library.api.service.impl.BookServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
        Assertions.assertThat(exception).isInstanceOf(IsbnDuplicatedException.class);
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

    @Test
    @DisplayName("Deve retornar nulo ao obter um livro não existente.")
    public void getNotFoundBookById() {

        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        Assertions.assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBook() {

        Book book = Book.builder().id(1L).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao deletar um livro.")
    public void deleteInvalidBook() {

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBook() {

        Long id = 1L;

        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createNewBook();
        updatedBook.setId(id);

        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao atualizar um livro.")
    public void updateInvalidBook() {

        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades.")
    public void findBooks() {

        Book book = createNewBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> listsBooks = Arrays.asList(book);

        Page<Book> page = new PageImpl<Book>(listsBooks, pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(listsBooks);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Book createNewBook() {
        return Book.builder()
                .author("Fulano")
                .title("As aventuras")
                .isbn("123")
                .build();
    }

}
