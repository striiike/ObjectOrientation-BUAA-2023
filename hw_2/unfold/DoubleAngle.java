package unfold;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;

public class DoubleAngle {
    public static Mono mergeDoubleAngle(Mono mono) {


        for (Map.Entry<Poly, Integer> polyEntry : mono.getSinArray().entrySet()) {

            Poly key = polyEntry.getKey();
            Integer value = polyEntry.getValue();

            Poly otherKey = new Poly();
            for (Map.Entry<Poly, Integer> otherEntry : mono.getSinArray().entrySet()) {
                if (Objects.equals(key, otherEntry.getKey())) {
                    otherKey = otherEntry.getKey();
                    break;
                }
            }

            if (Objects.equals(mono.getCosArray().get(otherKey), value)
                    && mono.getCoe().mod(new BigInteger("2")).equals(BigInteger.ZERO)
                    && value == 1) {
                mono.getSinArray().remove(key);
                mono.getCosArray().remove(otherKey);
                mono.setCoe(mono.getCoe().divide(new BigInteger("2")));

                Poly clone = key.clone();

                for (Mono monoClone : clone.getPoly()) {
                    monoClone.setCoe(
                            monoClone.getCoe().multiply(new BigInteger("2")));
                }
                mono.getSinArray().merge(clone, 1, Integer::sum);

                break;
            }


        }

        return mono;
    }
}
