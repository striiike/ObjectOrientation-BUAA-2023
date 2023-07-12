import com.oocourse.spec1.exceptions.PersonIdNotFoundException;

import java.util.HashMap;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private final int id;
    private static int counter = 0;
    private static final HashMap<Integer, Integer> EXCEPTION = new HashMap<>();

    public MyPersonIdNotFoundException(int id) {
        this.id = id;
        if (!EXCEPTION.containsKey(id)) {
            EXCEPTION.put(id, 0);
        }
    }

    @Override
    public void print() {
        counter++;
        EXCEPTION.merge(id, 1, Integer::sum);
        System.out.println("pinf-" + counter + ", " + id + "-" + EXCEPTION.get(id));
    }
}
