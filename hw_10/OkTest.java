import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OkTest {

    public static int check(
            int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
            ArrayList<HashMap<Integer, Integer>> afterData, int result) {

        HashMap<Integer, Integer> oldHeat = beforeData.get(0);
        HashMap<Integer, Integer> heat = afterData.get(0);
        HashMap<Integer, Integer> id = afterData.get(1);

        for (Map.Entry<Integer, Integer> entry : oldHeat.entrySet()) {
            if (entry.getValue() >= limit && !heat.containsKey(entry.getKey())) {
                return 1;
            }
            if (entry.getValue() < limit && heat.containsKey(entry.getKey())) {
                return 1;
            }
        }

        for (Map.Entry<Integer, Integer> entry : heat.entrySet()) {
            Integer key = entry.getKey();
            if (!oldHeat.containsKey(entry.getKey()) ||
                    !Objects.equals(oldHeat.get(key), entry.getValue())) {
                return 2;
            }
        }

        // really mentally disabled
        HashMap<Integer, Integer> oldId = beforeData.get(1);
        for (Integer i : heat.values()) { if (i < limit) { return 3; } }

        for (Map.Entry<Integer, Integer> entry : oldId.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (value != null && heat.containsKey(value) &&
                    id.containsValue(entry.getValue())) {
                if (!id.containsKey(key) || !Objects.equals(id.get(key), value)) {
                    return 5;
                }
            }
            if (value == null) {
                if (!id.containsKey(key) || !Objects.equals(id.get(key), value)) {
                    return 6;
                }
            }
        }

        int sum = 0;
        for (Map.Entry<Integer, Integer> entry : oldId.entrySet()) {
            if (entry.getValue() == null) {
                sum++;
            }
            if (entry.getValue() != null &&
                    heat.containsKey(entry.getValue()) &&
                    id.containsValue(entry.getValue())) {
                sum++;
            }
        }
        if (sum != id.size()) { return 7; }

        if (heat.size() != result) { return 8; }
        return 0;

    }
}
