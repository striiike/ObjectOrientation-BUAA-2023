package service;

import book.Book;
import main.Borrower;
import main.Magic;
import main.Output;

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
            System.out.println(Magic.getTime() + " " +
                    name + " refused lending " +
                    book.toString() + " to " + borrower.getStuNum());
            Output.printState(book, 2);
            Output.printSequence();
            return false;
        } else {
            borrower.addBook(book, Magic.getCurDate());
            System.out.println(Magic.getTime() + " " +
                    name + " lent " +
                    book.toString() + " to " + borrower.getStuNum());
            Output.printState(book, 3);
            Output.printSequence();
            System.out.println(Magic.getTime() + " " +
                    borrower.getStuNum() + " borrowed " +
                    book.toString() + " from " + name);
            return true;
        }
    }

    public void punish(Book book, Borrower borrower) {
        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " got punished by " + name);
        System.out.println(Magic.getTime() + " borrowing and returning librarian received " +
                borrower.getStuNum() + "'s fine");
    }

    public void returnBook(Book book, Borrower borrower, boolean isSmeared, boolean isNative) {

        if (borrower.removeBook(book, Magic.getCurDate())) {
            punish(book, borrower);
        }

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
