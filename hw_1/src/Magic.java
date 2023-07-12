import unfold.Poly;
import unit.Expr;

import java.util.Objects;
import java.util.Scanner;

public class Magic {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Lexer lexer = new Lexer(input.replaceAll("\\s+", ""));
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();

        // エクスプレッションの拡張
        Poly poly = expr.toPoly();

        System.out.println(Objects.equals(poly.toString(), "") ? "0" : poly.toString());


    }
}
