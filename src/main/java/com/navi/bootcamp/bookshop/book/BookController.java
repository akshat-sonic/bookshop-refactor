package com.navi.bootcamp.bookshop.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
public class BookController {
    private final BookService bookService;


    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    List<Book> list() {
        return bookService.fetchAll();
    }

    @PostMapping("/admin/books/uploadBooks")
    public ResponseEntity<?> uploadBooks(@RequestParam("file") MultipartFile file) {
        List<Book> books;
        try {
            if(file.isEmpty()) return new ResponseEntity<>("File is empty,",HttpStatus.BAD_REQUEST);
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream())) ;
            books = bookService.saveBooks(reader);
        } catch (IOException e) {
            return new ResponseEntity<>("File not found",HttpStatus.BAD_REQUEST);
        } catch(BookException ex) {
            return  new ResponseEntity<>(ex.errors,HttpStatus.BAD_REQUEST);
        }
        return  ResponseEntity.ok("File Uploaded Successfully");
    }

    @GetMapping("/books/search")
    public List<Book> search(@RequestParam String name) {
        System.out.println("book name 1 " + name);
        return bookService.search(name);
    }

    @GetMapping("/listOfBooksPerPage")
    public List<Book> searchBooksBasedOnPageNumber(@RequestParam("NoOfBooksPerPage") int pageSize, @RequestParam("pageNumber") int pageNumber) {
        List<Book> books = bookService.fetchBooksBasedOnPageNumber(pageSize, pageNumber);
        return books;

    }

}
