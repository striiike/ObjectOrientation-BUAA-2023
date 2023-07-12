package exception;

import com.oocourse.spec3.exceptions.EqualMessageIdException;

import java.util.HashMap;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private final int id;
    private static int counter = 0;
    private static final HashMap<Integer, Integer> EXCEPTION = new HashMap<>();

    public MyEqualMessageIdException(int id) {
        this.id = id;
        if (!EXCEPTION.containsKey(id)) {
            EXCEPTION.put(id, 0);
        }
    }

    @Override
    public void print() {
        counter++;
        EXCEPTION.merge(id, 1, Integer::sum);
        System.out.println("emi-" + counter + ", " + id + "-" + EXCEPTION.get(id));
    }
}
