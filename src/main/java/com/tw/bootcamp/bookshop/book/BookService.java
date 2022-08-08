package com.tw.bootcamp.bookshop.book;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

@Service
public class BookService {
    private final BookRepository bookRepository;
    String[] HEADERS = { "id","author","name","price","quantity","isbn"};
    private Map<String, Book> bookMap = new HashMap<>();

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    private Validator validator;

    public List<Book> fetchAll() {
        return bookRepository.findAllByOrderByPriceDesc();
    }


    public List<Book> saveBooks(Reader reader) throws IOException {
        List<Book> books = new ArrayList<>();
        //Reader reader = new FileReader(fileName) ;
        Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader(HEADERS)
                .withFirstRecordAsHeader()
                .parse(reader);
        for (CSVRecord record : records) {
            Book book = new Book(
                    record.get("author"),
                    record.get("title"),
                    record.get("price").isEmpty()? 0:Integer.parseInt(record.get("price")),
                    record.get("books_count").isEmpty()? 0:Long.parseLong(record.get("books_count")),
                    record.get("isbn"));

            if(book.getBooks_count() == 0){
                throw new BookException("Quantity is zero",record.get("title"));
            }

            java.util.Set<javax.validation.ConstraintViolation<Book>> errorList = validator.validate(book);
            if(!errorList.isEmpty()) {
                throw new
                        BookException("Exception in Validator",record.get("title"));
            }
            //To merge the count of Books from file
            if(bookMap.get(record.get("author")+":"+record.get("title"))==null) {
                bookMap.put(record.get("author")+":"+record.get("title"),book);
            } else {
                Book existingBook = bookMap.get(record.get("author")+":"+record.get("title"));
                book.setBooks_count(existingBook.getBooks_count() + book.getBooks_count());
            }

            books.add(book);
        }
        insertBooksInDB(books);
        bookMap.clear();
        return books;
    }

    public void insertBooksInDB(List<Book> inputFromFile) {
        for (Book bookFromCSV : inputFromFile) {
            Optional<Book> dbBook = bookRepository.findByNameAndAuthorName(bookFromCSV.getName(),bookFromCSV.getAuthorName());
            if(dbBook.isPresent()) {
                Book existingBook = dbBook.get();
                long sumOfBookCounts = existingBook.getBooks_count() + bookFromCSV.getBooks_count();
                existingBook.setBooks_count(sumOfBookCounts);
                existingBook.setPrice(bookFromCSV.getPrice());
                bookRepository.save(existingBook);
            } else {
                bookRepository.save(bookFromCSV);
            }
        }
    }

    public List<Book> search(String name) {
        List<Book> books = new ArrayList<Book>();
        books = bookRepository.findAllByNameContainingIgnoreCase(name);
        return books;
    }

    public List<Book> fetchBooksBasedOnPageNumber(int pageSize, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Book> pageBooks = bookRepository.findBooksByOrderByPriceDesc(pageable);
        return pageBooks.getContent();
    }

}
