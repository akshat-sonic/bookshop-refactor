**Bookshop Refactoring Steps:**

Checkout the [master](https://gitlab.com/tw-he-dev-bootcamp/base-repos/book-shop-refactor.git) branch.
Start by running all BookIntegrationTest. While performing the below steps ensure that the integration test always passed. Some of the BookControllerTest or BookServiceTest might change. 

You can find the refactored solution on the [all-tests-kinda-passing](https://gitlab.com/tw-he-dev-bootcamp/base-repos/book-shop-refactor/-/commits/all-tests-kinda-passing) branch for reference. 


1. Extract Book parsing logic into BookparserTest. 


2. Add more service Tests. 

```
@Test
public void shouldSaveBooks() throws IOException {
    String booksCsv = "id,author,title,price,books_count,isbn\n" +
            "51,Cassandra Clare,\"City of Bones (The Mortal Instruments, #1)\",1461,178,1416914285\n" +
            "52,Stephenie Meyer,\"Eclipse (Twilight, #3)\",2335,185,316160202";

    bookService.saveBooks(new StringReader(booksCsv));

    List<Book> allBooks = bookRepository.findAll();
    assertEquals(2, allBooks.size());
}

@Test
public void shouldFailSavingBooksWhenQuantityIsEmpty() throws IOException {
    String booksCsv = "id,author,title,price,books_count,isbn\n" +
            "51,Cassandra Clare,\"City of Bones (The Mortal Instruments, #1)\",1461,0,1416914285\n" +
            "52,Stephenie Meyer,\"Eclipse (Twilight, #3)\",2335,185,316160202";

    assertThrows(BookException.class, () -> {
        try(StringReader reader = new StringReader(booksCsv)){
            bookService.saveBooks(reader);
        }
    });
}
```

3.  Add Tests around upload at Service Level    
```
@Test
public void shouldAddToBookCountIfBookAlreadyExists() throws IOException {
    Book cityOfBones = new Book("City of Bones (The Mortal Instruments, #1)", "Cassandra Clare", 1461, 100L, "1416914285");
    bookRepository.save(cityOfBones);

    String booksCsv = "id,author,title,price,books_count,isbn\n" +
            "51,Cassandra Clare,\"City of Bones (The Mortal Instruments, #1)\",1461,178,1416914285\n" +
            "52,Stephenie Meyer,\"Eclipse (Twilight, #3)\",2335,185,316160202";

    bookService.saveBooks(new StringReader(booksCsv));

    List<Book> allBooks = bookRepository.findAll();
    List<Book> foundBooks = allBooks.stream().filter(book -> book.getIsbn().equals("1416914285")).collect(toList());
    assertEquals(1, foundBooks.size());
    assertEquals(278, foundBooks.get(0).getBooks_count());
}

@Test
public void shouldAddToBookCountIfBookIsRepeatedInCsv() throws IOException {
    Book cityOfBones = new Book("City of Bones (The Mortal Instruments, #1)", "Cassandra Clare", 1461, 100L, "1416914285");
    bookRepository.save(cityOfBones);

    String booksCsv = "id,author,title,price,books_count,isbn\n" +
            "51,Cassandra Clare,\"City of Bones (The Mortal Instruments, #1)\",1461,178,1416914285\n" +
            "52,Cassandra Clare,\"City of Bones (The Mortal Instruments, #1)\",1461,100,1416914285\n" +
            "53,Stephenie Meyer,\"Eclipse (Twilight, #3)\",2335,185,316160202";

    bookService.saveBooks(new StringReader(booksCsv));

    List<Book> allBooks = bookRepository.findAll();
    List<Book> foundBooks = allBooks.stream().filter(book -> book.getIsbn().equals("1416914285")).collect(toList());
    assertEquals(1, foundBooks.size());
    assertEquals(378, foundBooks.get(0).getBooks_count());
}

```
4.  Make BookParser a Spring service and include it with BookService. 
5.  Refactor / clean up  BookServiceTest  to use Book objects for setup , instead of raw CSV strings.   STEP 1
6.  Introduce BookParser Service at the Controller and Controller Tests level and complete refactoring. 
