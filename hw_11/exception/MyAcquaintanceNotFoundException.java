package exception;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

import java.util.HashMap;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {

    private final int id;
    private static int counter = 0;
    private static final HashMap<Integer, Integer> EXCEPTION = new HashMap<>();

    public MyAcquaintanceNotFoundException(int id) {
        this.id = id;
        if (!EXCEPTION.containsKey(id)) {
            EXCEPTION.put(id, 0);
        }
    }

    @Override
    public void print() {
        counter++;
        EXCEPTION.merge(id, 1, Integer::sum);
        System.out.println("anf-" + counter + ", " + id + "-" + EXCEPTION.get(id));
    }
}
