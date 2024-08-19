package unit;

import unfold.Mono;
import unfold.Poly;

import java.math.BigInteger;

public class SinFactor implements Factor {

    private BigInteger index;
    private Factor factor;

    public SinFactor(Factor factor, BigInteger index) {
        this.factor = factor;
        this.index = index;
    }

    public SinFactor(Factor factor) {
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
        mono.addSin(factor.toPoly(), index.intValueExact());
        Poly poly = new Poly();
        poly.addMono(mono);
        poly.simplify();
        return poly;
    }

    @Override
    public String toString() {
        return "sin((" + factor.toString() + "))**" + index.toString();
    }
}
