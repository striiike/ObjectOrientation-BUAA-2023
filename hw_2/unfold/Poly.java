package unfold;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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

    public void plusPoly(Poly polyToAdd) {
        this.poly.addAll(polyToAdd.getPoly());
        this.simplify();
    }

    public Poly mulPoly(Poly polyToMul) {
        Poly resPoly = new Poly();
        for (Mono monoToMul : polyToMul.getPoly()) {
            for (Mono mono : this.poly) {
                Mono resMono = Mono.mul(mono, monoToMul);
                resPoly.addMono(resMono);
            }
        }
        /* optimize here */
        /* seems that it doesn't have to do */
        /* actually it has,
           it accelerates greatly, Orz
         */
        resPoly.simplify();
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
            for (Mono mono : this.poly) {
                mono.adjustBase();
            }
            return this;
        } else {
            resPoly = this.clone();
            Poly factorPoly = this.clone();
            for (int i = 1; i < index; i++) {
                resPoly = resPoly.mulPoly(factorPoly);
            }
            for (Mono mono : resPoly.poly) {
                mono.adjustBase();
            }
            return resPoly;
        }
    }

    // become negative
    public void negatePoly() {
        for (Mono mono : this.poly) {
            mono.setCoe(mono.getCoe().negate());
        }
    }

    /* needs to be optimized */
    /* including merge the same suffix and deal cos(0) sin(0) */
    public void simplify() {
        int size = poly.size();

        for (int i = 0; i < size; i++) {
            poly.get(i).mergeSinCosZero();
            poly.get(i).adjustBase();

        }

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {

                if (poly.get(i).integrate(poly.get(j))) {
                    poly.remove(j);
                    i = 0;
                    j = i;
                    size--;
                    continue;
                } else if (poly.get(j).integrate(poly.get(i))) {
                    poly.remove(i);
                    i = 0;
                    j = i;
                    size--;
                    continue;
                }
                if (poly.get(i).mergeAble(poly.get(j))) {
                    poly.get(i).plus(poly.get(j));
                    poly.remove(j);
                    j = i;
                    size--;
                    continue;
                }

            }
        }
        for (int i = 0; i < size; i++) {
            poly.get(i).mergeSinCosZero();
            poly.get(i).adjustBase();
            // test
            poly.get(i).mergeDoubleAngle();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Poly poly1 = (Poly) o;
        Collections.sort(poly);
        Collections.sort(poly1.poly);
        return Objects.equals(poly, poly1.poly);
    }

    @Override
    public int hashCode() {
        Collections.sort(poly);
        return Objects.hash(poly);
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
            return /*((sign == 1) ? "(" : "")
                    + */res.toString().substring(1)
                    /* + ((sign == 1) ? "(" : "")*/;
        } else {
            return /*((sign == 1) ? "(" : "")
                    + */res.toString()
                    /*+ ((sign == 1) ? "(" : "")*/;
        }


    }

    @Override
    public Poly clone() {
        Poly clone = new Poly();
        for (Mono mono : this.poly) {
            Mono monoClone = mono.clone();
            clone.getPoly().add(monoClone);
        }
        return clone;

    }

}
