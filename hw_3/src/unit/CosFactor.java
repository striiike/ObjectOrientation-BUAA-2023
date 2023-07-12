package unit;

import unfold.Mono;
import unfold.Poly;

import java.math.BigInteger;

public class CosFactor implements Factor {

    private BigInteger index;
    private Factor factor;

    public CosFactor(Factor factor, BigInteger index) {
        this.factor = factor;
        this.index = index;
    }

    public CosFactor(Factor factor) {
        this.factor = factor;
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    @Override
    public Poly toPoly() {
        Mono mono = new Mono(BigInteger.ONE, BigInteger.ZERO,
                BigInteger.ZERO, BigInteger.ZERO);
        mono.addCos(factor.toPoly(), index.intValueExact());
        Poly poly = new Poly();
        poly.addMono(mono);
        poly.simplify();
        return poly;
    }

    @Override
    public String toString() {
        return "cos((" + factor.toString() + "))**" + index.toString();
    }
}
