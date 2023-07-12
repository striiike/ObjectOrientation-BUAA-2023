package service;

import book.Book;
import main.Borrower;
import main.Magic;

import java.util.HashMap;
import java.util.Map;

public class BorrowReturnLib {

    private HashMap<Book, Integer> bookshelf;

    private HashMap<Book, Integer> stagingBooks;

    private final String name = "borrowing and returning librarian";

    public BorrowReturnLib(HashMap<Book, Integer> bookshelf) {
        this.bookshelf = bookshelf;
        this.stagingBooks = new HashMap<>();
    }

    public boolean borrow(Book book, Borrower borrower) {
        bookshelf.merge(book, -1, Integer::sum);
        if (borrower.containsB()) {
            stagingBooks.merge(book, 1, Integer::sum);
            return false;
        } else {
            borrower.addBook(book);
            System.out.println(Magic.getTime() + " " +
                    borrower.getStuNum() + " borrowed " +
                    book.toString() + " from " + name);
            return true;
        }
    }

    public void punish(Book book, Borrower borrower) {
        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " got punished by " + name);
    }

    public void returnBook(Book book, Borrower borrower, boolean isSmeared) {
        borrower.removeBook(book);
        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " returned " +
                book.toString() + " to " + name);
        if (!isSmeared) {
            stagingBooks.merge(book, 1, Integer::sum);
        }
    }

    public void getAllStagingBooks() {
        for (Map.Entry<Book, Integer> entry : stagingBooks.entrySet()) {
            bookshelf.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        stagingBooks.clear();
    }

}
