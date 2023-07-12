import unit.Expr;
import unit.Factor;
import unit.Number;
import unit.Term;
import unit.Var;

import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parseExpr() {
        Expr expr = new Expr();
        expr.addTerm(parseTerm());
        expr.setIndex(BigInteger.ONE);
        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            expr.addTerm(parseTerm());
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        // sign of term
        term.setSign(lexer.peek().equals("-") ? -1 : 1);
        if (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            lexer.next();
        }

        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek().equals("(")) {
            lexer.next();
            Expr expr = parseExpr();
            lexer.next();
            if (lexer.peek().equals("**")) {
                lexer.next();
                expr.setIndex(new BigInteger(lexer.peek()));
                lexer.next();
            } else {
                expr.setIndex(BigInteger.ONE);
            }
            return expr;
        } else if (lexer.peek().equals("x")
                || lexer.peek().equals("y")
                || lexer.peek().equals("z")) {
            Var var = new Var(lexer.peek().charAt(0));
            lexer.next();
            if (lexer.peek().equals("**")) {
                lexer.next();
                var.setIndex(new BigInteger(lexer.peek()));
                lexer.next();
            } else {
                var.setIndex(BigInteger.ONE);
            }
            return var;

        } else {
            int sign = 1;
            if (lexer.peek().equals("+") || lexer.peek().equals("-")) {
                sign = lexer.peek().equals("+") ? 1 : -1;
                lexer.next();
            }
            BigInteger num = new BigInteger(lexer.peek());
            num = (sign == -1) ? num.negate() : num;
            lexer.next();
            return new Number(num);
        }
    }
}
