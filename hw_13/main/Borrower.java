package main;

import book.Book;

import java.util.ArrayList;
import java.util.Objects;

public class Borrower {

    private String stuNum;
    private ArrayList<Book> books;
    private ArrayList<Book> orderedBooks;
    private int orderedTimes;

    public Borrower(String stuNum) {
        this.books = new ArrayList<>();
        this.orderedBooks = new ArrayList<>();
        this.stuNum = stuNum;
        this.orderedTimes = 0;
    }

    public String getStuNum() {
        return stuNum;
    }

    public boolean containsB() {
        for (Book book : books) {
            if (Objects.equals(book.getCategory(), "B")) {
                return true;
            }
        }
        return false;
    }

    public boolean containsBook(Book book) {
        return books.contains(book);
    }

    public void addBook(Book book) {
        books.add(book);
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
        for (Book bookInPocket : books) {
            if (book.equals(bookInPocket)) {
                bookInPocket.setSmeared(true);
                break;
            }
        }
    }

    public boolean bookIsSmeared(Book book) {
        for (Book bookInPocket : books) {
            if (book.equals(bookInPocket)) {
                return bookInPocket.isSmeared();
            }
        }
        return false;
    }

    public void removeBook(Book book) {
        books.removeIf(book1 -> book1.equals(book)); // ::
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

}
