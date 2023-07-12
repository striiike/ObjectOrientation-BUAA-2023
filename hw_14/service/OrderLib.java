package service;

import book.Book;
import main.Borrower;
import main.Magic;
import main.Output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OrderLib {
    private final HashMap<Book, Integer> bookshelf;

    private final HashMap<Book, Integer> stagingBooks;
    private final PurchaseLib purchaseLib;

    private ArrayList<Borrower> orderPeople;
    private ArrayList<Book> orderBook;
    private final String name = "ordering librarian";

    public OrderLib(HashMap<Book, Integer> bookshelf, PurchaseLib purchaseLib) {
        this.bookshelf = bookshelf;
        this.stagingBooks = new HashMap<>();
        orderBook = new ArrayList<>();
        orderPeople = new ArrayList<>();
        this.purchaseLib = purchaseLib;
    }

    // background ensures lost won't lead to 0
    public boolean hasBookEver(Book book) {
        return bookshelf.containsKey(book);
    }

    public void order(Book book, Borrower borrower) {
        boolean validity = Objects.equals(book.getCategory(), "B")
                && !(borrower.containsB() || borrower.containsOrderBook(book));
        validity |= Objects.equals(book.getCategory(), "C")
                && !(borrower.containsBook(book) || borrower.containsOrderBook(book));
        validity &= !(borrower.getOrderedTimes() >= 3);

        if (!validity) {
            return;
        }

        orderPeople.add(borrower);
        orderBook.add(book);
        borrower.addOrderBook(book);

        if (!hasBookEver(book)) {
            book.setLentOut(true);
            purchaseLib.addBookToPurchase(book);
            purchaseLib.addRequestToPurchase(book, borrower);
        }

        System.out.println(Magic.getTime() + " " +
                borrower.getStuNum() + " ordered " +
                book.toString() + " from " + name);
        System.out.println(Magic.getTime() + " ordering librarian recorded " +
                borrower.getStuNum() + "'s order of " + book.toString());
    }

    // only for B, currently
    public void flushOrderedBooks(Borrower borrower) {
        borrower.flushOrderedBooks();
        for (int i = 0; i < orderBook.size(); ) {
            Book book = orderBook.get(i);
            Borrower borrowerOrdered = orderPeople.get(i);
            if (borrowerOrdered.equals(borrower) && book.getCategory().equals("B")) {
                orderPeople.remove(borrowerOrdered);
                orderBook.remove(book);
                i = 0;
            }
            i++;
        }
    }

    public void dispatchOrderBooks() {
        for (int i = 0; i < orderBook.size(); ) {
            Book book = orderBook.get(i);
            Borrower borrower = orderPeople.get(i);
            if (bookshelf.containsKey(book) && bookshelf.get(book) > 0) {

                // delete from order
                orderPeople.remove(borrower);
                orderBook.remove(book);
                borrower.removeOrder(book);

                // remove from bookshelf & dispatch
                bookshelf.merge(book, -1, Integer::sum);
                borrower.addBook(book);

                System.out.println(Magic.getTime() + " " +
                        name + " lent " +
                        book.toString() + " to " + borrower.getStuNum());
                Output.printState(book, 3);
                System.out.println(Magic.getTime() + " " +
                        borrower.getStuNum() + " borrowed " +
                        book.toString() + " from " + name);


                if (book.getCategory().equals("B")) {
                    flushOrderedBooks(borrower);
                }
                i = 0;
                continue;
            }
            i++;
        }
    }

}
