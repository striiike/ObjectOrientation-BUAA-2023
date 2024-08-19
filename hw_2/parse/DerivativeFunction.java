package parse;

import unfold.Mono;
import unfold.Poly;

import java.util.Map;
import java.util.Objects;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;

public class DerivativeFunction {

    public static Poly derivativePoly(Poly poly, String operator) {
        Poly resPoly = new Poly();
        for (Mono mono : poly.getPoly()) {
            Poly resMono = derivativeMono(mono, operator);
            resPoly.getPoly().addAll(resMono.getPoly());
        }
        resPoly.simplify();
        return resPoly;
    }

    public static Poly derivativeMono(Mono mono, String operator) {


        Mono monoVar = mono.clone();
        if (Objects.equals(operator, "x")) {
            monoVar.setCoe(monoVar.getIndexOfX()
                    .multiply(monoVar.getCoe()));
            monoVar.setIndexOfX(monoVar.getIndexOfX().add(ONE.negate()));
        }
        if (Objects.equals(operator, "y")) {
            monoVar.setCoe(monoVar.getIndexOfY()
                    .multiply(monoVar.getCoe()));
            monoVar.setIndexOfY(monoVar.getIndexOfY().add(ONE.negate()));
        }
        if (Objects.equals(operator, "z")) {
            monoVar.setCoe(monoVar.getIndexOfZ()
                    .multiply(monoVar.getCoe()));
            monoVar.setIndexOfZ(monoVar.getIndexOfZ().add(ONE.negate()));
        }
        Poly resPoly = new Poly();
        resPoly.getPoly().add(monoVar);


        for (Map.Entry<Poly, Integer> polyEntry : mono.getSinArray().entrySet()) {


            Mono sinReserved = mono.clone();
            sinReserved.getSinArray().remove(polyEntry.getKey());
            Mono sinDerived = new Mono(ONE, ZERO, ZERO, ZERO);



            if (polyEntry.getValue() != 1) {
                sinDerived.setCoe(valueOf(polyEntry.getValue()));
                sinDerived.getSinArray().put(polyEntry.getKey(), polyEntry.getValue() - 1);
            }
            sinDerived.getCosArray().put(polyEntry.getKey(), 1);

            Poly polyDerived = derivativePoly(polyEntry.getKey(), operator);
            Poly monoSin = sinDerived.toPoly().mulPoly(polyDerived).mulPoly(sinReserved.toPoly());

            resPoly.plusPoly(monoSin);

        }

        for (Map.Entry<Poly, Integer> polyEntry : mono.getCosArray().entrySet()) {


            Mono cosReserved = mono.clone();
            cosReserved.getCosArray().remove(polyEntry.getKey());

            Mono cosDerived = new Mono(ONE, ZERO, ZERO, ZERO);



            if (polyEntry.getValue() != 1) {
                cosDerived.setCoe(valueOf(polyEntry.getValue()));
                cosDerived.getCosArray().put(polyEntry.getKey(), polyEntry.getValue() - 1);
            }
            cosDerived.getSinArray().put(polyEntry.getKey(), 1);
            cosDerived.setCoe(cosDerived.getCoe().multiply(ONE.negate()));

            Poly polyDerived = derivativePoly(polyEntry.getKey(), operator);
            Poly monoCos = cosDerived.toPoly().mulPoly(polyDerived).mulPoly(cosReserved.toPoly());

            resPoly.plusPoly(monoCos);

        }

        resPoly.simplify();
        return resPoly;
    }

}
