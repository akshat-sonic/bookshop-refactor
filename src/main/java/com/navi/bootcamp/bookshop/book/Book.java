package com.navi.bootcamp.bookshop.book;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title is mandatory")
    private String name;
    @NotBlank(message = "Author is mandatory")
    private String authorName;
    @NotNull(message = "price is mandatory")
    @Min(value = 1, message = "Price should be greater than zero")
    private Integer price;
    private Long books_count;
    private String isbn;

    public Book() {
    }

    public Book(String name, String authorName, Integer price) {
        this.name = name;
        this.authorName = authorName;
        this.price = price;
    }


    public Book(String name, String authorName, Integer price,Long books_count,String isbn) {
        this.name = name;
        this.authorName = authorName;
        this.price = price;
        this.books_count = books_count;
        this.isbn = isbn;
    }

    public void setBooks_count(Long books_count) {
        this.books_count = books_count;
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public Integer getPrice() {
        return price;
    }

    public Long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public Long getBooks_count() {
        return books_count;
    }


    public void setPrice(Integer price) {
        this.price = price;
    }
}
