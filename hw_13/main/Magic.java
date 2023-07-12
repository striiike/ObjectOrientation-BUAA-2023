package main;

import book.Book;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Scanner;

public class Magic {

    private static LocalDate curDate = LocalDate.of(2023, 1, 1);
    private static LocalDate updateDate = LocalDate.of(2023, 1, 1);

    public static String getTime() {
        return "[" + curDate + "]";
    }

    public static boolean getDiff(LocalDate date) {
        return ChronoUnit.DAYS.between(curDate, date) > 0;
    }

    public static boolean needToArrange(LocalDate date) {
        if (ChronoUnit.DAYS.between(updateDate, date) >= 3) {
            curDate = updateDate.plusDays(3);
            while (ChronoUnit.DAYS.between(updateDate, date) >= 3) {
                updateDate = updateDate.plusDays(3);
            }
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {

        Lib lib = new Lib();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            String temp = scanner.next();
            Book book = new Book(temp.substring(0, 1), temp.substring(2));
            lib.addBook(book, scanner.nextInt());
        }

        int m = scanner.nextInt();
        for (int i = 0; i < m; i++) {
            String temp = scanner.next();
            LocalDate date = LocalDate.of(Integer.parseInt(temp.substring(1, 5)),
                    Integer.parseInt(temp.substring(6, 8)),
                    Integer.parseInt(temp.substring(9, 11)));

            if (getDiff(date)) {
                lib.flush();
            }
            if (needToArrange(date)) {
                lib.arrange();
            }

            curDate = date;

            String stuNum = scanner.next();
            lib.addBorrower(stuNum);

            temp = scanner.next();
            String bookTemp = scanner.next();
            Book book = new Book(bookTemp.substring(0, 1), bookTemp.substring(2));
            if (Objects.equals(temp, "borrowed")) {
                lib.dealBorrow(book, stuNum);
            } else if (Objects.equals(temp, "smeared")) {
                lib.dealSmear(book, stuNum);
            } else if (Objects.equals(temp, "lost")) {
                lib.dealLost(book, stuNum);
            } else if (Objects.equals(temp, "returned")) {
                lib.dealReturn(book, stuNum);
            }

        }
    }
}
