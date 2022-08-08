package com.tw.bootcamp.bookshop.book;

import com.tw.bootcamp.bookshop.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    UserService userService;

    @Test
    void shouldListAllBooksWhenPresent() throws Exception {
        List<Book> books = new ArrayList<>();
        Book book = new Book("title", "author name", 300);
        books.add(book);
        when(bookService.fetchAll()).thenReturn(books);

        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(bookService, times(1)).fetchAll();
    }

    @Test
    void shouldBeEmptyListWhenNoBooksPresent() throws Exception {
        when(bookService.fetchAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        verify(bookService, times(1)).fetchAll();
    }

    @Test
    void shouldSearchForBooksBySearchText() throws Exception {
        Book book = new Book("Thinking in Java", "Bruce Eckel", 500);

        when(bookService.search("java")).thenReturn(Arrays.asList(book));

        MockHttpServletRequestBuilder requestBuilder = get("/books/search")
                .queryParam("name", "java")
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(bookService, times(1)).search("java");
    }

    @Test
    void shouldBeAbleToDisplayBooksBasedOnPageNumber() throws Exception {
        Book book1 = new Book("Thinking in Java", "Bruce Eckel", 500);
        Book book2 = new Book("Thinking in Java", "Bruce Eckel", 5200);
        List<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);
        when(bookService.fetchBooksBasedOnPageNumber(10, 2)).thenReturn(books);
        mockMvc.perform(get("/listOfBooksPerPage")
                .param("NoOfBooksPerPage", "10")
                .param("pageNumber", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}