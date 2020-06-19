package com.example.library.api.resource;

import com.example.library.api.model.dto.BookDTO;
import com.example.library.api.model.entity.Book;
import com.example.library.api.service.BookService;
import com.example.library.api.service.exception.IsbnDuplicatedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBook() throws Exception {

        BookDTO dto = createNewBookDTO();

        Book savedBook = Book.builder()
                .id(10L)
                .author("Arthur")
                .title("As aventuras")
                .isbn("001")
                .build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(10L))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(dto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(dto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação para quando não houver dados suficiente para a criação do livro.")
    public void createInvalidBook() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createNewBookDTO();

        String json = new ObjectMapper().writeValueAsString(dto);

        String mensagemErro = "Isbn já cadastrado.";

        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new IsbnDuplicatedException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("error").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetails() throws Exception {

        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .author(createNewBookDTO().getAuthor())
                .title(createNewBookDTO().getTitle())
                .isbn(createNewBookDTO().getIsbn())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(book.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(book.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando um livro não existir.")
    public void bookNotFound() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBook() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar um livro para deletar.")
    public void deleteNonExistentBook() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() throws Exception {

        Long id = 1L;

        BookDTO newBook = createNewBookDTO();

        String json = new ObjectMapper().writeValueAsString(newBook);

        Book existsBook = Book.builder()
                .id(id)
                .author("some author")
                .title("some title")
                .isbn("321")
                .build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(existsBook));

        BDDMockito.given(service.update(existsBook)).willReturn(Book.builder()
                .id(id)
                .author(newBook.getAuthor())
                .title(newBook.getTitle())
                .isbn(existsBook.getIsbn())
                .build());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(newBook.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(newBook.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("321"));
    }

    @Test
    @DisplayName("Deve retornar resource not found ao tentar atualizar um livro inexistente.")
    public void updateNonExistentBook() throws Exception {

        Long id = 1L;

        BookDTO newBook = createNewBookDTO();

        String json = new ObjectMapper().writeValueAsString(newBook);

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("Deve listar livros utilizando filtros.")
    public void findBooks() throws Exception {

        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .author("some author")
                .title("some title")
                .isbn("321")
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=%s&size=%s", book.getTitle(), book.getAuthor(), 0, 100);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100));

    }

    private BookDTO createNewBookDTO() {
        return BookDTO.builder()
                .author("Arthur")
                .title("As aventuras")
                .isbn("001")
                .build();
    }

}
