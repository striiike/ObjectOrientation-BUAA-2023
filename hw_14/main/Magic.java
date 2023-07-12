package main;

import book.Book;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Magic {

    private static LocalDate curDate = LocalDate.of(2023, 1, 1);
    private static LocalDate updateDate = LocalDate.of(2023, 1, 1);

    private static ArrayList<Lib> libs = new ArrayList<>();

    private static Supervisor supervisor = new Supervisor(libs);

    public static String getTime() {
        return "[" + curDate + "]";
    }

    public static void pastOneDay() {
        curDate = curDate.plusDays(1);
    }

    public static boolean getDiff(LocalDate date) {
        return ChronoUnit.DAYS.between(curDate, date) > 0;
    }

    public static boolean needToArrange(LocalDate date) {
        if (ChronoUnit.DAYS.between(updateDate, date) >= 3) {
            curDate = updateDate.plusDays(3);
            updateDate = curDate;
            return true;
        } else {
            return false;
        }
    }

    public static Lib getLib(String stuNum) {

        StringBuilder schoolName = new StringBuilder();
        for (int i = 0; i < stuNum.length(); i++) {
            if (stuNum.charAt(i) != '-') {
                schoolName.append(stuNum.charAt(i));
            } else {
                break;
            }
        }

        for (Lib lib : libs) {
            if (schoolName.toString().equals(lib.getName())) {
                return lib;
            }
        }

        return libs.get(0);
    }

    public static Lib getLibFromName(String name) {
        for (Lib lib : libs) {
            if (name.toString().equals(lib.getName())) {
                return lib;
            }
        }

        return libs.get(0);
    }

    public static void initialLib(Scanner scanner) {

        int t = scanner.nextInt();
        for (int i = 0; i < t; i++) {
            Lib lib = new Lib(scanner.next(), supervisor);
            int n = scanner.nextInt();
            for (int j = 0; j < n; j++) {
                String temp = scanner.next();
                int count = scanner.nextInt();
                Book book = new Book(
                        temp.substring(0, 1),
                        temp.substring(2),
                        scanner.next(),
                        lib.getName());
                lib.addBook(book, count);
            }
            libs.add(lib);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initialLib(scanner);
        System.out.println("[2023-01-01] arranging librarian arranged all the books");
        int m = scanner.nextInt();
        for (int i = 0; i < m; i++) {
            String temp = scanner.next();

            LocalDate date = LocalDate.of(Integer.parseInt(temp.substring(1, 5)),
                    Integer.parseInt(temp.substring(6, 8)),
                    Integer.parseInt(temp.substring(9, 11)));

            // debug
            if (date.toString().equals("2023-01-30")) {
                int a;
                a = 0;
            }


            // after closing
            if (getDiff(date)) {
                for (Lib lib : libs) {
                    lib.flush();
                }
                supervisor.intercollegiateLent();
                // date change here, add one
                supervisor.moveOutAndIn(false);
                supervisor.dispatch();
            }

            // before opening, intercollegiate borrow


            // before opening
            if (needToArrange(date)) {


                for (Lib lib : libs) {
                    lib.purchaseBook();
                }
                System.out.println(Magic.getTime() + " arranging librarian arranged all the books");
                for (Lib lib : libs) {
                    lib.arrange();
                }

                while (ChronoUnit.DAYS.between(updateDate, date) >= 3) {
                    updateDate = updateDate.plusDays(3);
                    curDate = updateDate;
                    System.out.println(Magic.getTime() +
                            " arranging librarian arranged all the books");
                }
            }

            curDate = date;

            String stuNum = scanner.next();
            Lib lib = getLib(stuNum);
            lib.addBorrower(stuNum);

            temp = scanner.next();
            String bookTemp = scanner.next();
            Book book = new Book(bookTemp.substring(0, 1),
                    bookTemp.substring(2), null, lib.getName());
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
        supervisor.intercollegiateLent();
        supervisor.moveOutAndIn(true);
    }
}
