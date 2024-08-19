package parse;

import unit.Expr;
import unit.Factor;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomFunction {

    private static HashMap<String, String> exprMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> argvMap = new HashMap<>();

    public static void addCustomFunction(
            String funcName, String funcExpr, ArrayList<String> argvOfArray) {

        Lexer lexer = new Lexer(funcExpr);
        Parser parser = new Parser(lexer);
        Expr expr = parser.parseExpr();

        String finalExpr = expr.toPoly().toString();
        finalExpr = finalExpr.replaceAll("x", "u");
        finalExpr = finalExpr.replaceAll("y", "v");
        finalExpr = finalExpr.replaceAll("z", "w");

        exprMap.put(funcName, finalExpr);
        argvMap.put(funcName, argvOfArray);
    }

    public static String getCustomFunctionExpr(String funcName) {
        return exprMap.get(funcName);
    }

    public static ArrayList<String> getCustomFunctionArgv(String funcName) {
        return argvMap.get(funcName);
    }

    public static String call(String funcName, ArrayList<Factor> argv) {
        String funcExpr = exprMap.get(funcName);
        for (int i = 0; i < argv.size(); i++) {
            funcExpr = funcExpr.replaceAll(
                    argvMap.get(funcName).get(i),
                    "(" + argv.get(i).toString() + ")");
        }
        return funcExpr;
    }
}
