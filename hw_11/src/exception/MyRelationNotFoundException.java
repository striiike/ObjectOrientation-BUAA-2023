package exception;

import com.oocourse.spec3.exceptions.RelationNotFoundException;

import java.util.HashMap;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private final int id1;
    private final int id2;
    private static int counter = 0;
    private static final HashMap<Integer, Integer> EXCEPTION = new HashMap<>();

    public MyRelationNotFoundException(int id1, int id2) {
        this.id1 = Math.min(id1, id2);
        this.id2 = Math.max(id2, id1);
        if (!EXCEPTION.containsKey(id1)) {
            EXCEPTION.put(id1, 0);
        }
        if (!EXCEPTION.containsKey(id2)) {
            EXCEPTION.put(id2, 0);
        }
    }

    @Override
    public void print() {
        counter++;
        if (id1 == id2) {
            EXCEPTION.merge(id1, 1, Integer::sum);
        } else {
            EXCEPTION.merge(id1, 1, Integer::sum);
            EXCEPTION.merge(id2, 1, Integer::sum);
        }
        System.out.println("rnf-" + counter
                + ", " + id1 + "-" + EXCEPTION.get(id1)
                + ", " + id2 + "-" + EXCEPTION.get(id2));
    }
}
