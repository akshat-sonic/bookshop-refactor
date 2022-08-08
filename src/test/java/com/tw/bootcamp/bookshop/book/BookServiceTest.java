package com.tw.bootcamp.bookshop.book;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookServiceTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private Validator validator;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldFetchAllBooks() {
        Book book = new Book("title", "author name", 300);
        bookRepository.save(book);

        List<Book> books = bookService.fetchAll();

        assertEquals(1, books.size());
        assertEquals("title", books.get(0).getName());
    }

    @Test
    void shouldFetchAllBooksBeSortedByPrice() {
        Book lowPrice = new Book("title", "author name", 300);
        Book highPrice = new Book("costlier", "author name", 400);
        bookRepository.save(lowPrice);
        bookRepository.save(highPrice);

        List<Book> books = bookService.fetchAll();

        assertEquals(2, books.size());
        assertEquals("costlier", books.get(0).getName());
    }

    @Test
    void shouldBeAbleToSeeBooksWithMatchingName()
    {
        Book lowPrice = new Book("Tech Experts", "author name", 300);
        Book highPrice = new Book("costlier", "author name", 400);
        bookRepository.save(lowPrice);
        bookRepository.save(highPrice);

        List<Book> books = bookService.search("tech");
        assertEquals(1, books.size());
        assertEquals("Tech Experts", books.get(0).getName());
    }

    @Test
    void shouldNotBeAbleToSeeBooksWithOutMatchingName()
    {
        Book book = new Book("Tech Experts", "Jagadeesh K", 300);
        bookRepository.save(book);

        List<Book> books = bookService.search("Npci");
        assertEquals(0, books.size());
    }

    @Test
    void shouldBeAbleToSaveBooks(){
        List<Book> books = new ArrayList<Book>();
        Book book1 = new Book("Tech Experts", "Jagadeesh K", 300, (long) 128,"134567");
        Book book2 = new Book("Tech", "Sravani V", 300, (long) 134,"1345678");
        books.add(book1);
        books.add(book2);
        bookService.insertBooksInDB(books);

    }

    @Test
    void shouldSaveBookUpdatePriceAndQuantitySuccess() {
        Book bookFromCSV = new Book("Cassandra Clare", "City of Bones (The Mortal Instruments, #1)", 100, (long) 110, "1416914285");
        Optional<Book> booksFromDB = bookRepository.findByNameAndAuthorName(bookFromCSV.getName(), bookFromCSV.getAuthorName());
        if (booksFromDB.isPresent()) {
            Book dbBook = booksFromDB.get();
            long quantity = dbBook.getBooks_count() + bookFromCSV.getBooks_count();
            dbBook.setBooks_count(quantity);
            dbBook.setPrice(bookFromCSV.getPrice());
            Book result = bookRepository.save(dbBook);
            //  assertNotNull(result);
            assertEquals(result.getBooks_count(), 220);
            //   assertEquals(result.getBooks_count(),quantity);
        } else {
            Book result = bookRepository.save(bookFromCSV);
            assertEquals(result.getBooks_count(), 110);
        }
    }
    @Test
    void shouldValidationFailForAuthorEmpty() {
        Book book1 = new Book(null, "City of Bones (The Mortal Instruments, #1)",1462,
                190L, "1416914285");

        java.util.Set<javax.validation.ConstraintViolation<Book>> constraintViolations =  validator.validate(book1);

        assertEquals(constraintViolations.size() >0,true);

    }

    @Test
    void shouldBeAbleToGetListOfBooks(){
        Book book = new Book("Tech Experts", "Jagadeesh K", 300);
        Book book1 = new Book("Tech Experts", "Jagadeesh K", 5300);
        bookRepository.save(book);
        bookRepository.save(book1);
        List<Book> books = bookService.fetchBooksBasedOnPageNumber(1,1);
        assertEquals(1,books.size());
    }

}