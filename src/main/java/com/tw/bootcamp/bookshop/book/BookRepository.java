package com.tw.bootcamp.bookshop.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>  {
    List<Book> findAllByOrderByPriceDesc();

    List<Book> findAllByNameContainingIgnoreCase(String name);

    Optional<Book> findByNameAndAuthorName(String name, String authorName);

    Page<Book> findBooksByOrderByPriceDesc(Pageable pageable);

    Book findById(long id);
}

