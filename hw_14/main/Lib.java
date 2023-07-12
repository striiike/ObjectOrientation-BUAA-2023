package main;

import book.Book;
import service.BorrowReturnLib;
import service.LogisticsDiv;
import service.OrderLib;
import service.PurchaseLib;
import service.ServiceMac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Lib {

    private final String name;
    private final HashMap<Book, Integer> bookshelf = new HashMap<>();
    private final ArrayList<Borrower> borrowers = new ArrayList<>();
    private final BorrowReturnLib borrowReturnLib = new BorrowReturnLib(bookshelf);
    private final PurchaseLib purchaseLib = new PurchaseLib(bookshelf);
    private final OrderLib orderLib = new OrderLib(bookshelf, purchaseLib);
    private final ServiceMac serviceMac = new ServiceMac(bookshelf);
    private final LogisticsDiv logisticsDiv = new LogisticsDiv(bookshelf);
    private final Supervisor supervisor;

    private ArrayList<Book> reservedBooks = new ArrayList<>();
    private ArrayList<Borrower> reservedBorrowers = new ArrayList<>();

    public Lib(String name, Supervisor supervisor) {
        this.name = name;
        this.supervisor = supervisor;
    }

    public String getName() {
        return this.name;
    }

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

    // background ensures lost won't lead to 0
    public boolean hasBookEver(Book book) {
        return bookshelf.containsKey(book);
    }

    public boolean hasBookAndCanLent(Book book) {
        Book book1 = null;
        for (Map.Entry<Book, Integer> entry : bookshelf.entrySet()) {
            if (entry.getKey().equals(book)) {
                book1 = entry.getKey();
                break;
            }
        }
        return book1 != null && bookshelf.get(book) > 0
                && book1.canLentOut();
    }

    public void dealInterBorrow() {
        for (int i = 0; i < reservedBooks.size(); i++) {
            Book book = reservedBooks.get(i);
            Borrower borrower = reservedBorrowers.get(i);
            Lib lib;

            if ((lib = supervisor.checkValidAndBorrow(book, false)) != null) {
                if (supervisor.isDuplicate(book, borrower)) {
                    continue;
                }
                supervisor.checkValidAndBorrow(book, true);
                book.setSchool(lib.getName());
                supervisor.addRequest(book, borrower, lib);
            } else {
                // need deeper analysis
                orderLib.order(book, borrower);
            }
        }
        reservedBooks.clear();
        reservedBorrowers.clear();
    }

    public void purchaseBook() {
        purchaseLib.purchaseBook();
    }

    public void arrange() {
        borrowReturnLib.getAllStagingBooks();
        serviceMac.getAllStagingBooks();
        logisticsDiv.getAllStagingBooks();
        purchaseLib.getAllStagingBooks();
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
            // reserve request
            reservedBorrowers.add(borrower);
            reservedBooks.add(book);
            // orderLib.order(book, borrower);
        }
    }

    public void orderBook(Book book, Borrower borrower) {
        orderLib.order(book, borrower);
    }

    public void flushOrderBooks(Borrower borrower) {
        orderLib.flushOrderedBooks(borrower);
        purchaseLib.flushOrderedBooks(borrower);
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

    public void dealReturn(Book bookIn, String stuNum) {
        Borrower borrower = getBorrower(stuNum);
        boolean isSmeared = borrower.bookIsSmeared(bookIn);
        boolean isNative = borrower.bookIsNative(getName(), bookIn);
        Book book = borrower.getBook(bookIn);


        if (isSmeared) {
            borrowReturnLib.punish(book, borrower);
        }

        if (!isNative) {
            purchaseLib.addBookToReturn(book);
        }

        // borrower's book is gone
        if (book.getCategory().equals("B")) {
            borrowReturnLib.returnBook(book, borrower, isSmeared, isNative);
        } else if (book.getCategory().equals("C")) {
            serviceMac.returnBook(book, borrower, isSmeared, isNative);
        }

        if (isSmeared && isNative) {
            logisticsDiv.addBook(book);
        }
        if (isSmeared && !isNative) {
            System.out.println(Magic.getTime() + " " +
                    book.toString() + " got repaired by logistics division in " + getName());
            Output.printState(book, 7);
        }


    }

    public void flush() {
        for (Borrower borrower : borrowers) {
            borrower.flushOrderedTimes();
        }
    }

    // from purchasing lib for books need to return
    public void giveBooksToReturn(ArrayList<Book> books) {
        purchaseLib.giveBooksToReturn(books);
    }

    public void addBookToPurchaseLib(Book book) {
        purchaseLib.addToStagingBooks(book);
    }
}
