package unfold;

import java.math.BigInteger;
import java.util.Objects;

public class Mono {
    private BigInteger coe;
    private BigInteger indexOfX;
    private BigInteger indexOfY;
    private BigInteger indexOfZ;

    public Mono() {
    }

    public Mono(BigInteger coe, BigInteger indexOfX, BigInteger indexOfY, BigInteger indexOfZ) {
        this.coe = coe;
        this.indexOfX = indexOfX;
        this.indexOfY = indexOfY;
        this.indexOfZ = indexOfZ;

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
        BigInteger bigTwo = new BigInteger("2");

        if (coe.equals(BigInteger.ZERO)) {
            return "";
        }


        sb.append((coe.compareTo(BigInteger.ZERO) > 0) ? "+" : "-");
        if (Objects.equals(indexOfX, BigInteger.ZERO)
                && Objects.equals(indexOfY, BigInteger.ZERO)
                && Objects.equals(indexOfZ, BigInteger.ZERO)) {
            sb.append(coe.abs());
            return sb.toString();
        }

        /* optimize of omitting the coefficient */
        int coeFlag = 1;
        if (coe.abs().equals(BigInteger.ONE)) {
            coeFlag = 0;
        } else {
            sb.append(coe.abs());
        }

        if (indexOfX.compareTo(BigInteger.ONE) >= 0) {
            if (coeFlag == 1) {
                sb.append("*");
            }
            coeFlag = 1;
            sb.append(Objects.equals(indexOfX, BigInteger.ONE) ? "x" :
                    Objects.equals(indexOfX, bigTwo) ? "x*x" : ("x**" + indexOfX));
        }

        if (indexOfY.compareTo(BigInteger.ONE) >= 0) {
            if (coeFlag == 1) {
                sb.append("*");
            }
            coeFlag = 1;
            sb.append(Objects.equals(indexOfY, BigInteger.ONE) ? "y" :
                    Objects.equals(indexOfY, bigTwo) ? "y*y" : ("y**" + indexOfY));
        }
        if (indexOfZ.compareTo(BigInteger.ONE) >= 0) {
            if (coeFlag == 1) {
                sb.append("*");
            }
            coeFlag = 1;
            sb.append(Objects.equals(indexOfZ, BigInteger.ONE) ? "z" :
                    Objects.equals(indexOfZ, bigTwo) ? "z*z" : ("z**" + indexOfZ));
        }


        return sb.toString();
    }
}
