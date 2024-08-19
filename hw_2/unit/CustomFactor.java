package unit;

import parse.CustomFunction;
import parse.Lexer;
import parse.Parser;
import unfold.Poly;

import java.util.ArrayList;

public class CustomFactor implements Factor {
    private String funcExpr;

    private Expr expr; /* expr that is parsed */

    public CustomFactor(String funcName, ArrayList<Factor> argv) {
        this.funcExpr = CustomFunction.call(funcName, argv);
        this.parseExpr();
    }

    private void parseExpr() {
        Lexer lexer = new Lexer(this.funcExpr);
        Parser parser = new Parser(lexer);
        this.expr = parser.parseExpr();

    }

    public Expr getExpr() {
        return expr;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    @Override
    public Poly toPoly() {
        return expr.toPoly();
    }

    @Override
    public String toString() {
        return "(" + this.funcExpr + ")";
    }
}
