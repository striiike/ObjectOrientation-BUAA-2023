import parse.CustomFunction;
import parse.Lexer;
import parse.Parser;
import unfold.Poly;
import unit.Expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Magic {
    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);

        // custom functions
        int numOfFunc = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < numOfFunc; i++) {
            String funcExpr = scanner.nextLine().replaceAll("\\s+", "");

            /* I don't think this is necessary, though */


            String[] parts = funcExpr.split("=");

            String arguments = parts[0].replaceAll("x", "u");
            arguments = arguments.replaceAll("y", "v");
            arguments = arguments.replaceAll("z", "w");

            String[] argsOfFunc = arguments.substring(2, arguments.length() - 1)
                    .split(",");
            funcExpr = parts[1];

            String funcName = parts[0].substring(0, 1);
            ArrayList<String> argsOfArray = new ArrayList<>(Arrays.asList(argsOfFunc));
            CustomFunction.addCustomFunction(funcName, funcExpr, argsOfArray);
        }


        String input = scanner.nextLine();
        Lexer lexer = new Lexer(input.replaceAll("\\s+", ""));
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();

        Poly poly = expr.toPoly();
        System.out.println(poly.toString());


    }

}
