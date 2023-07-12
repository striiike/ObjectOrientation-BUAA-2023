package main;

import book.Book;

public class Output {

    private static String state1 = "state1";
    private static String state2 = "state2";
    private static String state3 = "state3";

    public static void printState(Book book, int num) {
        switch (num) {
            case 2:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state1 + " to " + state1);
                break;
            case 3:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state1 + " to " + state2);
                break;
            case 6:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state2 + " to " + state1);
                break;
            case 7:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state1 + " to " + state1);
                break;
            case 9:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state3 + " to " + state1);
                break;
            case 10:
                System.out.println("(State) " + Magic.getTime() + " " +
                        book.toLessString() + " transfers from " + state1 + " to " + state3);
                break;
            default:
                break;
        }
    }

}
