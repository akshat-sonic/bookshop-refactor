package com.navi.bootcamp.bookshop.book;

import java.util.ArrayList;

public class BookException extends RuntimeException {

    ArrayList<Object> errors;
    public BookException(String msg, String book) {
        super(msg);
        errors = new ArrayList<>();
        errors.add("Empty Value is passed for the Book");
        errors.add(book);
        errors.add(msg);

    }

    public BookException(String book_not_available_for_purchase, Long books_count) {
    }
}
