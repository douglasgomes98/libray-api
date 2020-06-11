package com.example.library.api.repository;

import com.example.library.api.model.entity.Book;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retonar verdadeiro quando existir um livro na base com o isbn informado.")
    public void returnTrueWhenIsbnExists() {

        Book book = createNewBook();

        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(book.getIsbn());

        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retonar falso quando não existir um livro na base com o isbn informado.")
    public void returnFalseWhenIsbnExists() {

        String isbn = "123";

        boolean exists = repository.existsByIsbn(isbn);

        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por Id.")
    public void findById() {

        Book book = createNewBook();

        entityManager.persist(book);

        Optional<Book> foundBook = repository.findById(book.getId());

        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBook() {

        Book book = createNewBook();

        Book savedBook = entityManager.persist(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBook() {

        Book book = createNewBook();

        entityManager.persist(book);

        Book foundBook = entityManager.find(Book.class, book.getId());

        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());

        Assertions.assertThat(deletedBook).isNull();
    }

    private static Book createNewBook() {
        return Book.builder()
                .author("Fulano")
                .title("As aventuras")
                .isbn("123")
                .build();
    }

}
