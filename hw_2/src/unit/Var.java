package unit;

import unfold.Mono;
import unfold.Poly;

import java.math.BigInteger;

public class Var implements Factor {
    private Character arg;
    private BigInteger index;

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public Var(Character arg) {
        this.arg = arg;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(BigInteger.ONE,
                arg == 'x' ? index : BigInteger.ZERO,
                arg == 'y' ? index : BigInteger.ZERO,
                arg == 'z' ? index : BigInteger.ZERO);
        poly.addMono(mono);
        return poly;
    }

    @Override
    public String toString() {
        return this.arg + "**" + index;
    }
}
