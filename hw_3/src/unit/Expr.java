package unit;

import unfold.Poly;

import java.math.BigInteger;
import java.util.ArrayList;

public class Expr implements Factor {
    private ArrayList<Term> terms;
    private BigInteger index;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        for (Term term : terms) {
            poly.plusPoly(term.toPoly());
        }
        poly = poly.powPoly(index.intValueExact());
        return poly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Term term : terms) {
            sb.append(term.toString());
        }
        return "(" + sb.toString() + ")**" + index;
    }

}
