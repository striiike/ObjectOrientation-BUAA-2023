package main;

import book.Book;
import service.BorrowReturnLib;
import service.LogisticsDiv;
import service.OrderLib;
import service.ServiceMac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Lib {

    private final HashMap<Book, Integer> bookshelf = new HashMap<>();

    private final ArrayList<Borrower> borrowers = new ArrayList<>();
    private final BorrowReturnLib borrowReturnLib = new BorrowReturnLib(bookshelf);
    private final OrderLib orderLib = new OrderLib(bookshelf);
    private final ServiceMac serviceMac = new ServiceMac(bookshelf);
    private final LogisticsDiv logisticsDiv = new LogisticsDiv(bookshelf);

    public Borrower getBorrower(String stuNum) {
        for (Borrower borrower : borrowers) {
            if (Objects.equals(borrower.getStuNum(), stuNum)) {
                return borrower;
            }
        }
        return null;
    }

    public void addBorrower(String studentNum) {
        for (Borrower borrower : borrowers) {
            if (Objects.equals(borrower.getStuNum(), studentNum)) {
                return;
            }
        }
        Borrower borrower = new Borrower(studentNum);
        borrowers.add(borrower);
    }

    public void addBook(Book book, int num) {
        bookshelf.merge(book, num, Integer::sum);
    }

    public void addBook(Book book) {
        bookshelf.merge(book, 1, Integer::sum);
    }

    public void removeBook(Book book) {
        bookshelf.merge(book, -1, Integer::sum);
    }

    public void arrange() {
        borrowReturnLib.getAllStagingBooks();
        serviceMac.getAllStagingBooks();
        logisticsDiv.getAllStagingBooks();
        orderLib.dispatchOrderBooks();
    }

    public void dealBorrow(Book book, String stuNum) {
        Borrower borrower = getBorrower(stuNum);
        if (serviceMac.query(book, borrower)) {
            if (book.getCategory().equals("B")) {
                if (borrowReturnLib.borrow(book, borrower)) {
                    orderLib.flushOrderedBooks(borrower);
                }
            } else if (Objects.equals(book.getCategory(), "C")) {
                serviceMac.borrow(book, borrower);
            }
        } else {
            orderLib.order(book, borrower);
        }
    }

    public void dealLost(Book book, String stuNum) {
        Borrower borrower = getBorrower(stuNum);
        borrower.removeBook(book);
        borrowReturnLib.punish(book, borrower);
    }

    public void dealSmear(Book book, String stuNum) {
        Borrower borrower = getBorrower(stuNum);
        borrower.smearBook(book);
    }

    public void dealReturn(Book book, String stuNum) {
        Borrower borrower = getBorrower(stuNum);
        boolean isSmeared = borrower.bookIsSmeared(book);

        if (isSmeared) {
            borrowReturnLib.punish(book, borrower);
        }

        if (book.getCategory().equals("B")) {
            borrowReturnLib.returnBook(book, borrower, borrower.bookIsSmeared(book));
        } else if (book.getCategory().equals("C")) {
            serviceMac.returnBook(book, borrower, borrower.bookIsSmeared(book));
        }

        if (isSmeared) {
            logisticsDiv.addBook(book);
        }
    }

    public void flush() {
        for (Borrower borrower : borrowers) {
            borrower.flushOrderedTimes();
        }
    }
}
