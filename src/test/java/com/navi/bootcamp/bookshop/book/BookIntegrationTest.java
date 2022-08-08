package com.navi.bootcamp.bookshop.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@ActiveProfiles("test")
public class BookIntegrationTest {

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void loadBooks() {
        bookRepository.save(new Book("To Kill a Mockingbird", "Harper Lee", 400, 10L, "ISBN1"));
        bookRepository.save(new Book("The Great Gatsby ", "F. Scott Fitzgerald", 680, 20L, "ISBN2"));
    }

    @AfterEach
    void clearBooks() {
        bookRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    private final Book book1 = new Book("To Kill a Mockingbird", "Harper Lee", 400, 10L, "ISBN1");
    private final Book book2 = new Book("The Great Gatsby ", "F. Scott Fitzgerald", 680, 20L, "ISBN2");

    private String getJsonString(Object input) {
        String result = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            result = objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Test
    public void shouldReturnAllBooksInTheLibrary() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/books").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2));
////                .andExpect(content().json(getJsonString(Arrays.asList(book1, book2))));

        mockMvc.perform(get("/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldBeAbleToLoadCSVFileAndInsertInTable() throws Exception {
        File file = ResourceUtils.getFile("classpath:books-50_latest.csv");
        FileInputStream inputstream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "books-50_latest.csv", "text/csv", inputstream);
        mockMvc.perform(multipart("/admin/books/uploadBooks").file(mockMultipartFile))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotBeAbleToLoadIfCSVFileIsEmpty() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("dummy.csv", "some".getBytes());
        mockMvc.perform(multipart("/admin/books/uploadBooks")
                        .file(mockMultipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenSearchTextIsEmptyShouldReturnAllBooksInTheLibrary() throws Exception {
        String searchParam = "";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search?name=" + searchParam)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonString(Arrays.asList(book1, book2))));
    }

    @Test
    public void whenSearchTextTitleShouldReturnAllBooksInTheLibraryThatMatchesEitherAuthorOrTitle() throws Exception {
        mockMvc.perform(get("/books/search?name=" + book1.getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonString(Arrays.asList(book1))));
    }


    @Test
    public void whenSearchTextPartialMatchShouldReturnAllBooksInTheLibraryThatMatchesEitherAuthorOrTitle() throws Exception {
        String searchParam = "Kill";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search?name=" + searchParam).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonString(Arrays.asList(book1))));
    }

    @Test
    public void whenThereAreMultipleMatchesShouldReturnAllBooksInTheLibraryThatMatchesEitherAuthorOrTitle() throws Exception {
        String searchParam = "a";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search?name=" + searchParam).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJsonString(Arrays.asList(book1, book2))));
    }

    @Test
    public void whenThereIsNoMatchShouldReturnEmpty() throws Exception {
        String searchParam = "can not really match";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search?name=" + searchParam).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    public void shouldReturnNotFoundStatusIfIDIsNotPresentInDb() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books/9999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(content().string("")).andReturn();
    }

    @Test
    public void shouldReturnNotFoundStatusIfIdIsInValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/books/bootcamp").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
    }

    @Test
    public void whenSpecialCharacterIsPresentInSearchParamShouldReturnBooksMatchingLiteral() throws Exception {
        String searchParam = "%Kill";
        mockMvc.perform(MockMvcRequestBuilders.get("/books/search?name=" + searchParam).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}