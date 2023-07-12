package unit;

import parse.DerivativeFunction;
import parse.Lexer;
import parse.Parser;
import unfold.Poly;

public class DerivationFactor implements Factor {
    private String diffOperator;
    private String exprToDerive;
    private String resExpr;
    private Expr expr;

    public DerivationFactor(String diffOperator, String exprToDerive) {
        this.diffOperator = diffOperator;
        this.exprToDerive = exprToDerive;
        this.parseExpr();
        this.resExpr = DerivativeFunction
                .derivativePoly(this.expr.toPoly(), this.diffOperator).toString();
    }

    private void parseExpr() {
        Lexer lexer = new Lexer(this.exprToDerive);
        Parser parser = new Parser(lexer);
        this.expr = parser.parseExpr();
    }

    @Override
    public Poly toPoly() {
        Lexer lexer = new Lexer(this.resExpr);
        Parser parser = new Parser(lexer);
        return parser.parseExpr().toPoly();
    }

    @Override
    public String toString() {
        return "(" + this.resExpr + ")";
    }
}
