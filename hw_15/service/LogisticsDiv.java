package service;

import book.Book;
import main.Magic;
import main.Output;

import java.util.HashMap;
import java.util.Map;

public class LogisticsDiv {
    private final HashMap<Book, Integer> bookshelf;

    private final HashMap<Book, Integer> stagingBooks;
    private final String name = "logistics division";

    public LogisticsDiv(HashMap<Book, Integer> bookshelf) {
        this.bookshelf = bookshelf;
        this.stagingBooks = new HashMap<>();
    }

    public void addBook(Book book) {
        stagingBooks.merge(book, 1, Integer::sum);
        System.out.println(Magic.getTime() + " " +
                book.toString() + " got repaired by " + name + " in " + book.getSchool());
        Output.printState(book, 7);
    }

    public void getAllStagingBooks() {
        for (Map.Entry<Book, Integer> entry : stagingBooks.entrySet()) {
            bookshelf.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        stagingBooks.clear();
    }
}
