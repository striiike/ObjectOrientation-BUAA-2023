import java.util.HashMap;

public class Dsu {
    private final HashMap<Integer, Integer> fatherMap;
    private final HashMap<Integer, Integer> sizeMap;

    public Dsu() {
        this.fatherMap = new HashMap<>();
        this.sizeMap = new HashMap<>();
    }

    public int find(int originId) {
        int ancestor = originId;
        while (fatherMap.get(ancestor) != ancestor) {
            ancestor = fatherMap.get(ancestor);
        }
        int id = originId;
        while (id != ancestor) {
            fatherMap.put(id, ancestor);
            id = fatherMap.get(id);
        }
        return ancestor;
    }

    public boolean putPerson(int id) {
        if (fatherMap.containsKey(id) || sizeMap.containsKey(id)) {
            return false;
        }
        fatherMap.put(id, id);
        sizeMap.put(id, 1);
        return true;
    }

    public boolean union(int id1, int id2) {
        int father1 = find(id1);
        int father2 = find(id2);
        if (father1 == father2) {
            return false;
        }
        if (sizeMap.get(father1) <= sizeMap.get(father2)) {
            fatherMap.put(father1, father2);
            sizeMap.put(father2, sizeMap.get(father1) + sizeMap.get(father2));
        } else {
            fatherMap.put(father2, father1);
            sizeMap.put(father1, sizeMap.get(father1) + sizeMap.get(father2));
        }
        return true;
    }

}
