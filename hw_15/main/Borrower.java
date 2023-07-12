package main;

import book.Book;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Borrower {

    private String stuNum;
    private HashMap<Book, LocalDate> books;
    private ArrayList<Book> orderedBooks;
    private int orderedTimes;

    public Borrower(String stuNum) {
        this.books = new HashMap<>();
        this.orderedBooks = new ArrayList<>();
        this.stuNum = stuNum;
        this.orderedTimes = 0;
    }

    public String getStuNum() {
        return stuNum;
    }

    public boolean containsB() {
        for (Book book : books.keySet()) {
            if (Objects.equals(book.getCategory(), "B")) {
                return true;
            }
        }
        return false;
    }

    public boolean containsBook(Book book) {
        return books.containsKey(book);
    }

    public void addBook(Book book, LocalDate date) {
        books.put(book, date);
    }

    public boolean containsOrderB() {
        for (Book book : orderedBooks) {
            if (Objects.equals(book.getCategory(), "B")) {
                return true;
            }
        }
        return false;
    }

    public boolean containsOrderBook(Book book) {
        return orderedBooks.contains(book);
    }

    public void addOrderBook(Book book) {
        orderedBooks.add(book);
        orderedTimes += 1;
    }

    public void smearBook(Book book) {
        for (Book bookInPocket : books.keySet()) {
            if (book.equals(bookInPocket)) {
                bookInPocket.setSmeared(true);
                break;
            }
        }
    }

    public boolean bookIsSmeared(Book book) {
        for (Book bookInPocket : books.keySet()) {
            if (book.equals(bookInPocket)) {
                return bookInPocket.isSmeared();
            }
        }
        return false;
    }

    public boolean removeBook(Book book, LocalDate date) {
        long time = ChronoUnit.DAYS.between(books.get(book), date);
        books.remove(book);
        if (book.getCategory().equals("B") && time > 30) {
            return true;
        }
        if (book.getCategory().equals("C") && time > 60) {
            return true;
        }
        return false;
    }

    public void flushOrderedTimes() {
        orderedTimes = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Borrower borrower = (Borrower) o;
        return Objects.equals(stuNum, borrower.stuNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stuNum);
    }

    // only for B
    public void flushOrderedBooks() {
        for (int i = 0; i < orderedBooks.size(); ) {
            Book book = orderedBooks.get(i);
            if (book.getCategory().equals("B")) {
                orderedBooks.remove(book);
                i = 0;
            }
            i++;
        }
    }

    public void removeOrder(Book book) {
        orderedBooks.removeIf(book1 -> book1.equals(book)); // ::
    }

    public int getOrderedTimes() {
        return this.orderedTimes;
    }

    public boolean bookIsNative(String name, Book book) {
        for (Book bookInPocket : books.keySet()) {
            if (book.equals(bookInPocket)) {
                return bookInPocket.getSchool().equals(name);
            }
        }
        return false;
    }

    public Book getBook(Book book) {
        for (Book book1 : books.keySet()) {
            if (book.equals(book1)) {
                return book1;
            }
        }
        return null;
    }

    public void getOrderedBook() {

    }

}
