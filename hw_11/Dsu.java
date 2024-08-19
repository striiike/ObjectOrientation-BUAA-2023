import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import static java.lang.Math.min;

public class Dsu {
    private final HashMap<Integer, Integer> fatherMap;
    private final HashMap<Integer, Integer> sizeMap;
    private final ArrayList<Integer> list1;
    private final ArrayList<Integer> list2;

    public Dsu() {
        this.fatherMap = new HashMap<>();
        this.sizeMap = new HashMap<>();
        this.list1 = new ArrayList<>();
        this.list2 = new ArrayList<>();
    }

    public int find(int originId, Map<Integer, Integer> parent) {
        int ancestor = originId;
        while (parent.get(ancestor) != ancestor) {
            ancestor = parent.get(ancestor);
        }
        int id = originId;
        while (id != ancestor) {
            parent.put(id, ancestor);
            id = parent.get(id);
        }
        return ancestor;
    }

    public boolean isCircle(int id1, int id2) {
        return find(id1, fatherMap) == find(id2, fatherMap);
    }

    public void putPerson(int id) {
        if (fatherMap.containsKey(id) || sizeMap.containsKey(id)) {
            return;
        }
        fatherMap.put(id, id);
        sizeMap.put(id, 1);
    }

    public boolean union(int id1, int id2, boolean reforge) {
        if (!reforge) {
            list1.add(id1);
            list2.add(id2);
        }
        int father1 = find(id1, fatherMap);
        int father2 = find(id2, fatherMap);
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

    public boolean deleteEdge(int id1, int id2) {
        for (Integer i : fatherMap.keySet()) {
            fatherMap.put(i, i);
            sizeMap.put(i, 1);
        }
        int index = 0;
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i) == id1 && list2.get(i) == id2
                    || list2.get(i) == id1 && list1.get(i) == id2) {
                index = i;
                continue;
            }
            union(list1.get(i), list2.get(i), true);
        }
        list1.remove(index);
        list2.remove(index);
        return find(id1, fatherMap) != find(id2, fatherMap);
    }

    public int queryLeastMoments(int start, HashMap<Integer, Person> peopleMap) {
        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> parent = new HashMap<>();
        dijkstra(dist, parent, peopleMap, start);
        int ans = Integer.MAX_VALUE;
        Person startPerson = peopleMap.get(start);
        for (Integer i : peopleMap.keySet()) {
            Person person = peopleMap.get(i);
            if (parent.containsKey(i) && find(i, parent) != i && startPerson.isLinked(person)) {
                ans = min(ans, dist.get(i) + person.queryValue(startPerson));
            }
        }

        for (Integer i : peopleMap.keySet()) {
            Person person1 = peopleMap.get(i);
            for (Person person2 : ((MyPerson) peopleMap.get(i)).getAcquaintanceMap().keySet()) {
                int j = person2.getId();
                if (i != start && j != start &&
                        parent.containsKey(i) && parent.containsKey(j) &&
                        find(i, parent) != find(j, parent) && person1.isLinked(person2)) {
                    ans = min(ans, dist.get(i) + dist.get(j) +
                            person1.queryValue(person2));
                }
            }
        }
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    public void dijkstra(
            Map<Integer, Integer> dist,
            Map<Integer, Integer> parent,
            HashMap<Integer, Person> graph,
            int start) {

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        Set<Integer> visited = new HashSet<>();

        for (Integer i : graph.keySet()) {
            dist.put(i, Integer.MAX_VALUE);
        }
        dist.put(start, 0);
        pq.offer(new int[]{start, 0});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0];
            int distance = curr[1];

            if (visited.contains(node)) {
                continue;
            }
            visited.add(node);

            for (Map.Entry<Person, Integer> entry :
                    ((MyPerson) graph.get(node)).getAcquaintanceMap().entrySet()) {
                int neighborNode = entry.getKey().getId();
                int neighborDistance = entry.getValue();

                if (!visited.contains(neighborNode)) {
                    int newDistance = distance + neighborDistance;
                    if (newDistance < dist.get(neighborNode)) {
                        dist.put(neighborNode, newDistance);
                        // root is exclusive
                        if (node == start) {
                            parent.put(neighborNode, neighborNode);
                        } else {
                            parent.put(neighborNode, node);
                        }

                        pq.offer(new int[]{neighborNode, newDistance});
                    }
                }
            }
        }
    }

}
