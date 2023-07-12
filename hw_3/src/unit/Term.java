package unit;

import unfold.Mono;
import unfold.Poly;

import java.math.BigInteger;
import java.util.ArrayList;

public class Term {
    private int sign;

    private ArrayList<Factor> factors;

    public Term() {
        this.factors = new ArrayList<>();
    }

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    public int getSign() {
        return sign;
    }

    public void setSign(int sign) {
        this.sign = sign;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(BigInteger.ONE, BigInteger.ZERO,
                BigInteger.ZERO, BigInteger.ZERO);
        poly.addMono(mono);
        for (Factor factor : factors) {
            poly = poly.mulPoly(factor.toPoly());
        }
        if (sign == -1) {
            poly.negatePoly();
        }
        return poly;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Factor factor : factors) {
            sb.append(factor.toString());
            sb.append("*");
        }
        return (sign > 0 ? "+" : "-") + sb.toString().substring(0, sb.length() - 1);
    }

}
