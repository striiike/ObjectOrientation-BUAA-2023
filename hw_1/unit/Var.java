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

    @Override
    public String toString() {
        return this.arg + "**" + index;
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        switch (arg) {
            case 'x': {
                Mono mono = new Mono(BigInteger.ONE, index,
                        BigInteger.ZERO, BigInteger.ZERO);
                poly.addMono(mono);
                break;
            }
            case 'y': {
                Mono mono = new Mono(BigInteger.ONE, BigInteger.ZERO,
                        index, BigInteger.ZERO);
                poly.addMono(mono);
                break;
            }
            case 'z': {
                Mono mono = new Mono(BigInteger.ONE, BigInteger.ZERO,
                        BigInteger.ZERO, index);
                poly.addMono(mono);
                break;
            }
            default:
                break;
        }


        return poly;
    }
}
