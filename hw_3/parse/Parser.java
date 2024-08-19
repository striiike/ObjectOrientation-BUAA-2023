package parse;

import unit.CosFactor;
import unit.CustomFactor;
import unit.DerivationFactor;
import unit.Expr;
import unit.Factor;
import unit.Number;
import unit.SinFactor;
import unit.Term;
import unit.Var;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

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
            expr.setIndex(parseExponent());
            return expr;
        } else if (lexer.peek().equals("x")
                || lexer.peek().equals("y")
                || lexer.peek().equals("z")) {
            Var var = new Var(lexer.peek().charAt(0));
            lexer.next();
            var.setIndex(parseExponent());
            return var;
        }
        // sin and cos
        else if (lexer.peek().equals("sin(")) {
            lexer.next();
            return parseSin();
        } else if (lexer.peek().equals("cos(")) {
            lexer.next();
            return parseCos();
        }
        // custom function
        else if (lexer.peek().equals("f(")
                || lexer.peek().equals("g(")
                || lexer.peek().equals("h(")) {
            String funcName = lexer.peek().substring(0, 1);
            lexer.next();
            return parseCustomFunction(funcName);
        }
        // derivative function
        else if (lexer.peek().charAt(0) == 'd') {
            String diffOperator = lexer.peek().substring(1, 2);
            lexer.next();
            return parseDerivationFunction(diffOperator);
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

    // parse the exponent following expression OR var OR sin/cos
    public BigInteger parseExponent() {
        if (lexer.peek().equals("**")) {
            lexer.next();
            BigInteger exp = new BigInteger(lexer.peek());
            lexer.next();
            return exp;
        } else {
            return BigInteger.ONE;
        }
    }

    public SinFactor parseSin() {
        SinFactor sinFactor = new SinFactor(parseFactor());
        lexer.next();
        sinFactor.setIndex(parseExponent());
        return sinFactor;
    }

    public CosFactor parseCos() {
        CosFactor cosFactor = new CosFactor(parseFactor());
        lexer.next();
        cosFactor.setIndex(parseExponent());
        return cosFactor;
    }

    public CustomFactor parseCustomFunction(String funcName) {
        ArrayList<Factor> argvOfArrays = new ArrayList<>();
        argvOfArrays.add(parseFactor());
        while (lexer.peek().equals(",")) {
            lexer.next();
            argvOfArrays.add(parseFactor());
        }
        lexer.next();
        return new CustomFactor(funcName, argvOfArrays);

    }

    public DerivationFactor parseDerivationFunction(String diffOperator) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int bracketMatch = 1;
        while (bracketMatch > 0) {
            sb.append(lexer.peek());
            if (Objects.equals(lexer.peek(), "(")
                    || Objects.equals(lexer.peek(), "sin(")
                    || Objects.equals(lexer.peek(), "cos(")
                    || Objects.equals(lexer.peek(), "f(")
                    || Objects.equals(lexer.peek(), "g(")
                    || Objects.equals(lexer.peek(), "h(")
                    || lexer.peek().charAt(0) == 'd') {
                bracketMatch++;
            } else if (Objects.equals(lexer.peek(), ")")) {
                bracketMatch--;
            }
            lexer.next();
        }
        return new DerivationFactor(diffOperator, sb.toString());
    }

}
