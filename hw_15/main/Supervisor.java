package main;

import book.Book;

import java.util.ArrayList;
import java.util.HashMap;

public class Supervisor {

    private final HashMap<Book, Integer> stagingBooks;
    private final ArrayList<Lib> libs;

    private ArrayList<Book> requestBook = new ArrayList<>();
    private ArrayList<Borrower> requestBorrower = new ArrayList<>();
    private ArrayList<Lib> requestToSchool = new ArrayList<>();

    public Supervisor(ArrayList<Lib> libs) {
        this.libs = libs;
        this.stagingBooks = new HashMap<>();
    }

    public void addRequest(Book book, Borrower borrower, Lib lib) {
        requestBook.add(book);
        requestBorrower.add(borrower);
        requestToSchool.add(lib);
    }

    // request contains B or the same
    // the man has B or the same
    public boolean isDuplicate(Book book, Borrower borrower) {
        if ((book.getCategory().equals("B") && borrower.containsB())
                || borrower.containsBook(book)) {
            return true;
        }
        if (book.getCategory().equals("B")) {
            for (int i = 0; i < requestBorrower.size(); i++) {
                if (borrower == requestBorrower.get(i) &&
                        requestBook.get(i).getCategory().equals("B")) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < requestBorrower.size(); i++) {
                if (borrower == requestBorrower.get(i) &&
                        requestBook.get(i).equals(book)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Lib checkValidAndBorrow(Book book, boolean borrow) {
        for (Lib lib : libs) {
            // already judge can be lent out
            if (lib.hasBookAndCanLent(book)) {
                // need deeper analysis
                if (borrow) {
                    lib.removeBook(book);
                }
                return lib;
            }
        }
        return null;
    }

    public void intercollegiateLent() {
        for (Lib lib : libs) {
            lib.dealInterBorrow();
        }
    }

    public void moveOutAndIn(boolean stop) {

        // lend out
        for (int i = 0; i < requestBorrower.size(); i++) {
            Lib lib = requestToSchool.get(i);
            Book book = requestBook.get(i);
            Borrower borrower = requestBorrower.get(i);
            System.out.println(Magic.getTime() + " " + book.toString() +
                    " got transported by purchasing department in " + lib.getName());
            Output.printState(book, 10);

        }

        // return out
        ArrayList<Book> booksToReturn = new ArrayList<>();
        for (Lib lib : libs) {
            ArrayList<Book> books = new ArrayList<>();
            lib.giveBooksToReturn(books);
            booksToReturn.addAll(books);
            for (Book book : books) {
                System.out.println(Magic.getTime() + " " + book.toString() +
                        " got transported by purchasing department in " + lib.getName());
                Output.printState(book, 10);
            }
        }

        if (stop) {
            return;
        }
        Magic.pastOneDay();

        // lend in
        for (int i = 0; i < requestBorrower.size(); i++) {
            Lib lib = requestToSchool.get(i);
            Book book = requestBook.get(i);
            Borrower borrower = requestBorrower.get(i);
            System.out.println(Magic.getTime() + " " + book.toString() +
                    " got received by purchasing department in " +
                    Magic.getLib(borrower.getStuNum()).getName());
            Output.printState(book, 9);
        }

        // return in
        for (Book book : booksToReturn) {
            Lib lib = Magic.getLibFromName(book.getSchool());
            lib.addBookToPurchaseLib(book);
            System.out.println(Magic.getTime() + " " + book.toString() +
                    " got received by purchasing department in " + book.getSchool());
            Output.printState(book, 9);
        }


    }

    public void dispatch() {
        for (int i = 0; i < requestBorrower.size(); i++) {
            Book book = requestBook.get(i);
            Borrower borrower = requestBorrower.get(i);
            Lib lib = Magic.getLib(borrower.getStuNum());


            borrower.addBook(book, Magic.getCurDate());
            if (book.getCategory().equals("B")) {
                lib.flushOrderBooks(borrower);
            }
            System.out.println(Magic.getTime() + " " +
                    "purchasing department" + " lent " +
                    book.toString() + " to " + borrower.getStuNum());
            Output.printState(book, 3);
            System.out.println(Magic.getTime() + " " +
                    borrower.getStuNum() + " borrowed " +
                    book.toString() + " from " + "purchasing department");
        }
        requestBook.clear();
        requestBorrower.clear();
        requestToSchool.clear();
    }

}
