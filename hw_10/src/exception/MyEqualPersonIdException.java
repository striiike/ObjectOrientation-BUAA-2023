package exception;

import com.oocourse.spec3.exceptions.EqualPersonIdException;

import java.util.HashMap;

public class MyEqualPersonIdException extends EqualPersonIdException {

    private final int id;
    private static int counter = 0;
    private static final HashMap<Integer, Integer> EXCEPTION = new HashMap<>();

    public MyEqualPersonIdException(int id) {
        this.id = id;
        if (!EXCEPTION.containsKey(id)) {
            EXCEPTION.put(id, 0);
        }
    }

    @Override
    public void print() {
        counter++;
        EXCEPTION.merge(id, 1, Integer::sum);
        System.out.println("epi-" + counter + ", " + id + "-" + EXCEPTION.get(id));
    }
}
