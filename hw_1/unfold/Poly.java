package unfold;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;

public class Poly {
    private ArrayList<Mono> poly;

    public ArrayList<Mono> getPoly() {
        return this.poly;
    }

    public Poly() {
        this.poly = new ArrayList<>();
    }

    public void addMono(Mono mono) {
        this.poly.add(mono);
    }

    public Poly plusPoly(Poly polyToAdd) {
        for (Mono monoToAdd : polyToAdd.getPoly()) {
            // can be merged or not
            int flag = 1;
            for (Mono mono : this.poly) {
                if (Objects.equals(mono.getIndexOfX(), monoToAdd.getIndexOfX())
                        && Objects.equals(mono.getIndexOfY(), monoToAdd.getIndexOfY())
                        && Objects.equals(mono.getIndexOfZ(), monoToAdd.getIndexOfZ())) {
                    mono.setCoe(mono.getCoe().add(monoToAdd.getCoe()));
                    flag = 0;
                    break;
                }
            }
            if (flag == 1) {
                this.poly.add(monoToAdd);
            }

        }
        return this;
    }

    public Poly mulPoly(Poly polyToMul) {
        Poly resPoly = new Poly();
        for (Mono monoToMul : polyToMul.getPoly()) {
            for (Mono mono : this.poly) {
                Mono resMono = new Mono(mono.getCoe().multiply(monoToMul.getCoe()),
                        mono.getIndexOfX().add(monoToMul.getIndexOfX()),
                        mono.getIndexOfY().add(monoToMul.getIndexOfY()),
                        mono.getIndexOfZ().add(monoToMul.getIndexOfZ()));
                resPoly.addMono(resMono);
            }
        }
        /* optimize here */
        /* seems that it doesn't have to do */
        return resPoly;

    }

    // pow poly
    public Poly powPoly(int index) {
        Poly resPoly = new Poly();
        if (index == 0) {
            Mono mono = new Mono(BigInteger.ONE, BigInteger.ZERO
                    , BigInteger.ZERO, BigInteger.ZERO);
            resPoly.addMono(mono);
            return resPoly;
        }
        if (index == 1) {
            return this;
        } else {
            resPoly = this;
            for (int i = 1; i < index; i++) {
                resPoly = resPoly.mulPoly(this);
            }
            return resPoly;
        }
    }

    // become negative
    public void negatePoly() {
        for (Mono mono : this.poly) {
            mono.setCoe(BigInteger.ZERO.subtract(mono.getCoe()));
        }
    }

    @Override
    public String toString() {

        // only 0
        if (poly.size() == 1) {
            if (poly.get(0).getCoe().equals(BigInteger.ZERO)) {
                return "0";
            }
        }
        StringBuilder res = new StringBuilder();

        // find one positive
        Mono monoPos = new Mono(); // this is ptr
        int findFlag = 0;
        for (Mono mono : this.poly) {
            if (mono.getCoe().compareTo(BigInteger.ZERO) > 0) {
                monoPos = mono;
                findFlag = 1;
                res.append(monoPos.toString());
                break;
            }
        }


        for (Mono mono : this.poly) {
            if (findFlag == 1) {
                if (monoPos == mono) {
                    continue;
                }
            }
            res.append(mono.toString());
        }
        if (findFlag == 1) {
            return res.toString().substring(1);
        } else {
            return res.toString();
        }

    }
}
