package unfold;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mono implements Comparable<Mono> {
    private BigInteger coe;
    private BigInteger indexOfX;
    private BigInteger indexOfY;
    private BigInteger indexOfZ;

    private HashMap<Poly, Integer> sinArray;
    private HashMap<Poly, Integer> cosArray;

    public Mono() {
        sinArray = new HashMap<>();
        cosArray = new HashMap<>();

    }

    public Mono(BigInteger coe, BigInteger indexOfX, BigInteger indexOfY, BigInteger indexOfZ) {
        this.coe = coe;
        this.indexOfX = indexOfX;
        this.indexOfY = indexOfY;
        this.indexOfZ = indexOfZ;

        sinArray = new HashMap<>();
        cosArray = new HashMap<>();
    }

    public BigInteger getCoe() {
        return coe;
    }

    public void setCoe(BigInteger coe) {
        this.coe = coe;
    }

    public BigInteger getIndexOfX() {
        return indexOfX;
    }

    public void setIndexOfX(BigInteger indexOfX) {
        this.indexOfX = indexOfX;
    }

    public BigInteger getIndexOfY() {
        return indexOfY;
    }

    public void setIndexOfY(BigInteger indexOfY) {
        this.indexOfY = indexOfY;
    }

    public BigInteger getIndexOfZ() {
        return indexOfZ;
    }

    public void setIndexOfZ(BigInteger indexOfZ) {
        this.indexOfZ = indexOfZ;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (coe.equals(BigInteger.ZERO)) {
            return "0";
        }
        sb.append((coe.compareTo(BigInteger.ZERO) >= 0) ? "+" : "-");
        /* optimize of omitting the coefficient */
        if (!coe.abs().equals(BigInteger.ONE)) {
            sb.append(coe.abs());
        }
        sb = appendVar(sb, "x", indexOfX);
        sb = appendVar(sb, "y", indexOfY);
        sb = appendVar(sb, "z", indexOfZ);
        sb = appendSinCos(sb, "sin", sinArray);
        sb = appendSinCos(sb, "cos", cosArray);

        if (sb.length() == 1) {
            sb.append(coe.abs());
        }
        return sb.toString();
    }

    public StringBuilder appendVar(StringBuilder sb, String name, BigInteger index) {
        if (index.compareTo(BigInteger.ZERO) == 0) {
            return sb;
        }
        if (sb.length() > 1) {
            sb.append("*");
        }
        BigInteger bigTwo = new BigInteger("2");
        sb.append(Objects.equals(index, BigInteger.ONE) ? name :
                Objects.equals(index, bigTwo) ? name + "*" + name :
                        (name + "**" + index));
        return sb;
    }

    public StringBuilder appendSinCos(
            StringBuilder sb, String name, HashMap<Poly, Integer> array) {
        if (array.size() == 0) {
            return sb;
        }
        for (Map.Entry<Poly, Integer> entry : array.entrySet()) {
            if (sb.length() > 1) {
                sb.append("*");
            }

            boolean isFactor = false;
            Poly poly = entry.getKey();
            Mono mono = poly.getPoly().get(0);
            boolean onlyCoe = (mono.getSinArray().size() + mono.getCosArray().size() == 0
                    && mono.indexOfX.compareTo(BigInteger.ZERO) == 0
                    && mono.indexOfY.compareTo(BigInteger.ZERO) == 0
                    && mono.indexOfZ.compareTo(BigInteger.ZERO) == 0);

            boolean onlyTri = (mono.getSinArray().size() + mono.getCosArray().size() == 1
                    && mono.indexOfX.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfY.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfZ.compareTo(BigInteger.ONE) < 0);
            boolean onlyX = (mono.getSinArray().size() + mono.getCosArray().size() != 1
                    && mono.indexOfX.compareTo(BigInteger.ONE) >= 0
                    && mono.indexOfY.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfZ.compareTo(BigInteger.ONE) < 0);
            boolean onlyY = (mono.getSinArray().size() + mono.getCosArray().size() != 1
                    && mono.indexOfX.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfY.compareTo(BigInteger.ONE) >= 0
                    && mono.indexOfZ.compareTo(BigInteger.ONE) < 0);
            boolean onlyZ = (mono.getSinArray().size() + mono.getCosArray().size() != 1
                    && mono.indexOfX.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfY.compareTo(BigInteger.ONE) < 0
                    && mono.indexOfZ.compareTo(BigInteger.ONE) >= 0);

            boolean onlyFactor = (Objects.equals(mono.getCoe(), BigInteger.ONE) &&
                    (onlyX || onlyY || onlyZ || onlyTri));


            if (poly.getPoly().size() == 1
                    && (onlyCoe || onlyFactor)) {
                isFactor = true;
            }
            if (isFactor) {
                BigInteger bigTwo = new BigInteger("2");
                if (mono.indexOfX.compareTo(bigTwo) == 0) {
                    sb.append(name).append("(x**2)");
                } else if (mono.indexOfY.compareTo(bigTwo) == 0) {
                    sb.append(name).append("(y**2)");
                } else if (mono.indexOfZ.compareTo(bigTwo) == 0) {
                    sb.append(name).append("(z**2)");
                } else {
                    sb.append(name).append("(").append(entry.getKey().toString()).append(")");
                }
            } else {
                sb.append(name).append("((").append(entry.getKey().toString()).append("))");
            }
            if (entry.getValue() > 1) {
                sb.append("**").append(entry.getValue());
            }
        }
        return sb;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Mono mono = (Mono) o;
        return Objects.equals(coe, mono.coe)
                && Objects.equals(indexOfX, mono.indexOfX)
                && Objects.equals(indexOfY, mono.indexOfY)
                && Objects.equals(indexOfZ, mono.indexOfZ)
                && Objects.equals(sinArray, mono.sinArray)
                && Objects.equals(cosArray, mono.cosArray);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coe, indexOfX, indexOfY, indexOfZ, sinArray, cosArray);
    }

    // have the same index and sin/cos OR one of the coe is zero
    // omit the sinx^2 + cosx^2 = 1
    public boolean mergeAble(Object obj) {
        if (obj instanceof Mono) {
            Mono mono = (Mono) obj;
            boolean coePart = mono.getIndexOfX().equals(this.getIndexOfX())
                    && mono.getIndexOfY().equals(this.getIndexOfY())
                    && mono.getIndexOfZ().equals(this.getIndexOfZ());
            boolean exactSame = Objects.equals(mono.getSinArray(), this.sinArray)
                    && Objects.equals(mono.getCosArray(), this.cosArray);
            return (coePart && exactSame)
                    || mono.getCoe().equals(BigInteger.ZERO)
                    || this.getCoe().equals(BigInteger.ZERO);
        }
        return false;
    }

    public boolean integrate(Mono other) {
        Mono comparePoly = this.clone();

        return integrateSin(other, comparePoly) || integrateCos(other, comparePoly);
    }

    public boolean integrateSin(Mono other, Mono comparePoly) {
        for (Map.Entry<Poly, Integer> polyEntry : this.sinArray.entrySet()) {
            if (polyEntry.getValue() >= 2) {
                Poly key = polyEntry.getKey();
                Integer value = polyEntry.getValue();

                Poly otherKey = new Poly();
                for (Map.Entry<Poly, Integer> otherEntry : other.getCosArray().entrySet()) {
                    if (Objects.equals(key, otherEntry.getKey())) {
                        otherKey = otherEntry.getKey();
                        break;
                    }
                }

                Integer otherValue = other.getCosArray().getOrDefault(otherKey, 0);

                comparePoly.getSinArray().remove(key);
                if (value > 2) {
                    comparePoly.getSinArray().put(
                            key, value - 2);
                }

                if (otherValue >= 2) {

                    other.getCosArray().remove(otherKey);

                    if (otherValue > 2) {
                        other.getCosArray().put(
                                otherKey, otherValue - 2);
                    }

                    if (Objects.equals(comparePoly, other)) {
                        this.getSinArray().remove(key);
                        if (value > 2) {
                            this.getSinArray().put(
                                    key, value - 2);
                        }
                        return true;
                    } else {
                        other.getCosArray().merge(otherKey, 2, Integer::sum);
                    }
                } else {

                    comparePoly.coe = comparePoly.coe.negate();
                    if (Objects.equals(comparePoly, other)) {
                        this.getSinArray().remove(key);
                        if (value > 2) {
                            this.getSinArray().put(
                                    key, value - 2);
                        }
                        this.cosArray.put(key, 2);
                        this.coe = this.coe.negate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean integrateCos(Mono other, Mono comparePoly) {
        for (Map.Entry<Poly, Integer> polyEntry : this.cosArray.entrySet()) {
            if (polyEntry.getValue() >= 2) {
                Poly key = polyEntry.getKey();
                Integer value = polyEntry.getValue();

                Poly otherKey = new Poly();
                for (Map.Entry<Poly, Integer> otherEntry : other.getSinArray().entrySet()) {
                    if (Objects.equals(key, otherEntry.getKey())) {
                        otherKey = otherEntry.getKey();
                        break;
                    }
                }

                comparePoly.getCosArray().remove(key);
                if (value > 2) {
                    comparePoly.getCosArray().put(
                            key, value - 2);
                }

                Integer otherValue = other.getSinArray().getOrDefault(otherKey, 0);
                if (otherValue >= 2) {


                    other.getSinArray().remove(otherKey);
                    if (otherValue > 2) {
                        other.getSinArray().put(
                                otherKey, otherValue - 2);
                    }

                    if (Objects.equals(comparePoly, other)) {
                        this.getCosArray().remove(key);
                        if (value > 2) {
                            this.getCosArray().put(
                                    key, value - 2);
                        }
                        return true;
                    } else {
                        other.getSinArray().merge(otherKey, 2, Integer::sum);
                    }
                } else {
                    comparePoly.coe = this.coe.negate();
                    if (Objects.equals(comparePoly, other)) {
                        this.getCosArray().remove(key);
                        if (value > 2) {
                            this.getCosArray().put(
                                    key, value - 2);
                        }
                        this.sinArray.put(key, 2);
                        this.coe = this.coe.negate();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void mergeDoubleAngle() {
        DoubleAngle.mergeDoubleAngle(this);
    }

    // just merge the coe
    public void plus(Mono mono) {
        if (mono.getCoe().equals(BigInteger.ZERO)) {
            return;
        }
        if (this.getCoe().equals(BigInteger.ZERO)) {
            this.coe = mono.getCoe();
            this.indexOfX = mono.getIndexOfX();
            this.indexOfY = mono.getIndexOfY();
            this.indexOfZ = mono.getIndexOfZ();
            this.setSinArray(mono.getSinArray());
            this.setCosArray(mono.getCosArray());
            return;
        }
        this.coe = this.coe.add(mono.getCoe());
    }

    public static Mono mul(Mono mono, Mono monoToMul) {
        Mono resMono = new Mono(mono.getCoe().multiply(monoToMul.getCoe()),
                mono.getIndexOfX().add(monoToMul.getIndexOfX()),
                mono.getIndexOfY().add(monoToMul.getIndexOfY()),
                mono.getIndexOfZ().add(monoToMul.getIndexOfZ()));

        for (Map.Entry<Poly, Integer> polyEntry : mono.getSinArray().entrySet()) {
            resMono.getSinArray().put(polyEntry.getKey().clone(), polyEntry.getValue());
        }
        for (Map.Entry<Poly, Integer> polyEntry : mono.getCosArray().entrySet()) {
            resMono.getCosArray().put(polyEntry.getKey().clone(), polyEntry.getValue());
        }


        for (Map.Entry<Poly, Integer> polyIntegerEntry : monoToMul.getSinArray().entrySet()) {
            resMono.getSinArray().merge((Poly) ((Map.Entry<?, ?>) polyIntegerEntry).getKey(),
                    (Integer) ((Map.Entry<?, ?>) polyIntegerEntry).getValue(), Integer::sum);
        }

        for (Map.Entry<Poly, Integer> polyIntegerEntry : monoToMul.getCosArray().entrySet()) {
            resMono.getCosArray().merge((Poly) ((Map.Entry<?, ?>) polyIntegerEntry).getKey(),
                    (Integer) ((Map.Entry<?, ?>) polyIntegerEntry).getValue(), Integer::sum);
        }
        return resMono;
    }

    /* kill cos(0) and sin(0) */
    public void mergeSinCosZero() {
        for (Map.Entry<Poly, Integer> polyEntry : this.sinArray.entrySet()) {
            if (Objects.equals(polyEntry.getKey().toString(), "0")) {
                this.coe = BigInteger.ZERO;
                this.indexOfX = BigInteger.ZERO;
                this.indexOfY = BigInteger.ZERO;
                this.indexOfZ = BigInteger.ZERO;
                this.getSinArray().clear();
                this.getCosArray().clear();
                return;
            }
        }
        for (Map.Entry<Poly, Integer> polyEntry : this.cosArray.entrySet()) {
            if (Objects.equals(polyEntry.getKey().toString(), "0")) {
                this.getCosArray().remove(polyEntry.getKey());
            }
        }


    }

    public void adjustBase() {
        for (Map.Entry<Poly, Integer> polyEntry : this.sinArray.entrySet()) {
            if (polyEntry.getKey().getPoly().size() == 1
                    && polyEntry.getValue() % 2 == 0) {
                if (polyEntry.getKey().getPoly().get(0).getCoe()
                        .compareTo(BigInteger.ZERO) <= 0) {
                    polyEntry.getKey().getPoly().get(0)
                            .setCoe(polyEntry.getKey().getPoly().get(0).getCoe().abs());
                }
            }
            // newly added
            if (polyEntry.getKey().getPoly().size() == 1
                    && polyEntry.getValue() % 2 != 0) {
                if (polyEntry.getKey().getPoly().get(0).getCoe()
                        .compareTo(BigInteger.ZERO) <= 0) {
                    polyEntry.getKey().getPoly().get(0)
                            .setCoe(polyEntry.getKey().getPoly().get(0).getCoe().abs());
                    this.setCoe(this.getCoe().negate());
                }
            }
        }
        for (Map.Entry<Poly, Integer> polyEntry : this.cosArray.entrySet()) {
            if (polyEntry.getKey().getPoly().size() == 1) {
                if (polyEntry.getKey().getPoly().get(0).getCoe()
                        .compareTo(BigInteger.ZERO) <= 0) {
                    polyEntry.getKey().getPoly().get(0)
                            .setCoe(polyEntry.getKey().getPoly().get(0).getCoe().abs());
                }
            }
        }
    }

    public HashMap<Poly, Integer> getSinArray() {
        return sinArray;
    }

    public void setSinArray(HashMap<Poly, Integer> sinArray) {
        this.sinArray = sinArray;
    }

    public HashMap<Poly, Integer> getCosArray() {
        return cosArray;
    }

    public void setCosArray(HashMap<Poly, Integer> cosArray) {
        this.cosArray = cosArray;
    }

    public void addSin(Poly poly, Integer index) {
        if (index == 0) {
            return;
        }
        sinArray.put(poly, index);
    }

    public void addCos(Poly poly, Integer index) {
        if (index == 0) {
            return;
        }
        cosArray.put(poly, index);
    }

    @Override
    public Mono clone() {
        Mono clone = new Mono(
                this.coe, this.indexOfX, this.indexOfY, this.indexOfZ);
        HashMap<Poly, Integer> sinClone = new HashMap<>();
        HashMap<Poly, Integer> cosClone = new HashMap<>();
        for (Map.Entry<Poly, Integer> polyEntry : this.sinArray.entrySet()) {
            sinClone.put(polyEntry.getKey().clone(), polyEntry.getValue());
        }
        for (Map.Entry<Poly, Integer> polyEntry : this.cosArray.entrySet()) {
            cosClone.put(polyEntry.getKey().clone(), polyEntry.getValue());
        }
        clone.setSinArray(sinClone);
        clone.setCosArray(cosClone);
        return clone;

    }

    @Override
    public int compareTo(Mono other) {
        return this.toString().compareTo(other.toString());
    }

    public Poly toPoly() {
        Poly poly = new Poly();
        poly.getPoly().add(this.clone());
        return poly;
    }

}
