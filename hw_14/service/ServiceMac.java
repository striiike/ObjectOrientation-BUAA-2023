package service;

import book.Book;
import main.Borrower;
import main.Magic;
import main.Output;

import java.util.HashMap;
import java.util.Map;

public class ServiceMac {
    private HashMap<Book, Integer> bookshelf;

    private HashMap<Book, Integer> stagingBooks;
    private final String name = "self-service machine";

    public ServiceMac(HashMap<Book, Integer> bookshelf) {
        this.bookshelf = bookshelf;
        this.stagingBooks = new HashMap<>();
    }

    public boolean borrow(Book book, Borrower borrower) {
        bookshelf.merge(book, -1, Integer::sum);
        if (borrower.containsBook(book)) {
            stagingBooks.merge(book, 1, Integer::sum);
            System.out.println(Magic.getTime() + " " +
                    name + " refused lending " +
                    book.toString() + " to " + borrower.getStuNum());
            Output.printState(book, 2);
            return false;
        } else {
            borrower.addBook(book);

            System.out.println(Magic.getTime() + " " +
                    name + " lent " +
                    book.toString() + " to " + borrower.getStuNum());
            Output.printState(book, 3);
            System.out.println(Magic.getTime() + " " +
                    borrower.getStuNum() + " borrowed " +
                    book.toString() + " from " + name);

            return true;
        }
    }

    public boolean query(Book book, Borrower borrower) {
        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " queried " +
                book.toLessString() + " from " + name);
        System.out.println(Magic.getTime() + " self-service machine provided information of " +
                book.toLessString());
        return bookshelf.containsKey(book) && bookshelf.get(book) > 0;


    }

    public void returnBook(Book book, Borrower borrower, boolean isSmeared, boolean isNative) {
        borrower.removeBook(book);

        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " returned " +
                book.toString() + " to " + name);
        System.out.println(Magic.getTime() + " " +
                name + " collected " +
                book.toString() + " from " + borrower.getStuNum());
        Output.printState(book, 6);
        if (!isSmeared && isNative) {
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
