package service;

import book.Book;
import main.Borrower;
import main.Magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.max;

public class PurchaseLib {
    private final HashMap<Book, Integer> bookshelf;
    private final HashMap<Book, Integer> stagingBooks;

    private final String name = "purchasing department";

    private final HashMap<Book, Integer> purchaseList;
    private final ArrayList<Book> bookToReturn;

    private final ArrayList<Borrower> requestBorrowers;
    private final ArrayList<Book> requestBooks;

    public PurchaseLib(HashMap<Book, Integer> bookshelf) {
        this.bookshelf = bookshelf;
        this.stagingBooks = new HashMap<>();
        this.purchaseList = new HashMap<>();
        this.bookToReturn = new ArrayList<>();
        this.requestBooks = new ArrayList<>();
        this.requestBorrowers = new ArrayList<>();

    }

    public void addBookToPurchase(Book book) {
        purchaseList.merge(book, 1, Integer::sum);
    }

    public void addRequestToPurchase(Book book, Borrower borrower) {
        requestBorrowers.add(borrower);
        requestBooks.add(book);
    }

    public void purchaseBook() {
        ArrayList<Book> purchased = new ArrayList<>();
        for (Book book: requestBooks) {
            if (purchased.contains(book) || !purchaseList.containsKey(book)) {
                continue;
            }
            bookshelf.merge(book, max(purchaseList.get(book), 3), Integer::sum);
            purchased.add(book);
            System.out.println(Magic.getTime() + " " + book +
                    " got purchased by purchasing department in " +
                    book.getSchool());
        }
        purchaseList.clear();
        requestBooks.clear();
        requestBorrowers.clear();
    }

    public void addBookToReturn(Book book) {
        bookToReturn.add(book);
    }

    public void giveBooksToReturn(ArrayList<Book> books) {
        books.addAll(bookToReturn);
        bookToReturn.clear();
    }

    public void addToStagingBooks(Book book) {
        stagingBooks.merge(book, 1, Integer::sum);
    }

    public void getAllStagingBooks() {
        for (Map.Entry<Book, Integer> entry : stagingBooks.entrySet()) {
            bookshelf.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        stagingBooks.clear();
    }

    public void flushOrderedBooks(Borrower borrower) {


        for (int i = 0; i < requestBorrowers.size(); i++) {
            Borrower borrower1 = requestBorrowers.get(i);
            Book book = requestBooks.get(i);

            // only B
            if (borrower == borrower1 && book.getCategory().equals("B")) {
                purchaseList.merge(book, -1, Integer::sum);
                if (purchaseList.get(book) == 0) {
                    purchaseList.remove(book);
                }
            }

        }
    }
}
