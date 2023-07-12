package unit;

import unfold.Mono;
import unfold.Poly;

import java.math.BigInteger;

// save integer without prefix zero, and with pos|neg
public class Number implements Factor {
    private BigInteger num;

    public Number(BigInteger num) {
        this.num = num;
    }

    public Poly toPoly() {
        Mono mono = new Mono(num, BigInteger.ZERO
                , BigInteger.ZERO, BigInteger.ZERO);
        Poly poly = new Poly();
        poly.addMono(mono);
        return poly;
    }

    @Override
    public String toString() {
        return String.valueOf(num);
    }

}
