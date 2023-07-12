import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MyNetwork implements Network {

    private Person[] people;

    private final Dsu dsu;
    private final HashMap<Integer, Person> peopleMap;
    private int blockSum = 0;
    private int tripleSum = 0;

    public MyNetwork() {
        this.dsu = new Dsu();
        this.peopleMap = new HashMap<>();
    }

    @Override
    public boolean contains(int id) {
        return peopleMap.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        return peopleMap.get(id);
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (contains(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());
        }
        peopleMap.put(person.getId(), person);
        ((MyPerson) person).setDsu(this.dsu);
        dsu.putPerson(person.getId());
        blockSum++;
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        MyPerson person1 = ((MyPerson) peopleMap.get(id1));
        MyPerson person2 = ((MyPerson) peopleMap.get(id2));
        person1.getAcquaintanceMap().put(person2, value);
        person2.getAcquaintanceMap().put(person1, value);
        if (dsu.union(id1, id2)) {
            blockSum--;
        }


        MyPerson forSum1 = null;
        MyPerson forSum2 = null;
        if (person1.getAcquaintanceMap().size() > person2.getAcquaintanceMap().size()) {
            forSum1 = person2;
            forSum2 = person1;
        } else {
            forSum1 = person1;
            forSum2 = person2;
        }
        ArrayList<Person> temp = new ArrayList<>(forSum1.getAcquaintanceMap().keySet());
        for (Person person : temp) {
            if (((MyPerson) person).getAcquaintanceMap().containsKey(forSum2)) {
                tripleSum++;
            }
        }




    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return peopleMap.get(id1).queryValue(peopleMap.get(id2));
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        return dsu.find(id1) == dsu.find(id2);
    }

    @Override
    public int queryBlockSum() {
        return blockSum;
    }

    @Override
    public int queryTripleSum() {
        return tripleSum;
        /*
        HashMap<Integer, ArrayList<Integer>> map = new HashMap<>();
        HashMap<Integer, Integer> dgr = new HashMap<>();
        for (Person person : peopleMap.values()) {
            dgr.put(person.getId(), ((MyPerson) person).getAcquaintanceMap().size());
            map.put(person.getId(), new ArrayList<Integer>());
        }
        for (Person person : peopleMap.values()) {
            for (Person acq : ((MyPerson) person).getAcquaintanceMap().keySet()) {
                if (dgr.get(person.getId()) < dgr.get(acq.getId())) {
                    map.get(person.getId()).add(acq.getId());
                } else if ((Objects.equals(dgr.get(person.getId()), dgr.get(acq.getId()))
                        && person.getId() < acq.getId())) {
                    map.get(person.getId()).add(acq.getId());
                }
            }
        }
        HashMap<Integer, Integer> visTime = new HashMap<>();
        int result = 0;
        for (Integer u : map.keySet()) {
            for (Integer v : map.get(u)) {
                visTime.put(v, u);
            }
            for (Integer v : map.get(u)) {
                for (Integer w : map.get(v)) {
                    if (Objects.equals(visTime.get(w), u)) {
                        ++result;
                    }
                }
            }
        }

        return result;*/
    }

    @Override
    public boolean queryTripleSumOKTest(
            HashMap<Integer, HashMap<Integer, Integer>> beforeData,
            HashMap<Integer, HashMap<Integer, Integer>> afterData, int result) {
        int realResult = 0;
        ArrayList<Integer> keySet = new ArrayList<>(beforeData.keySet());
        int i;
        int j;
        int k;
        for (i = 0; i < keySet.size(); ++i) {
            for (j = i + 1; j < keySet.size(); ++j) {
                for (k = j + 1; k < keySet.size(); ++k) {
                    if (beforeData.get(keySet.get(i)).containsKey(keySet.get(j))
                            && beforeData.get(keySet.get(j)).containsKey(keySet.get(k))
                            && beforeData.get(keySet.get(k)).containsKey(keySet.get(i))) {
                        realResult++;
                    }
                }
            }
        }
        if (result != realResult || beforeData.size() != afterData.size()) {
            return false;
        }
        for (Integer it : beforeData.keySet()) {
            if (!afterData.containsKey(it)) {
                return false;
            }
            if (afterData.get(it).size() != beforeData.get(it).size()) {
                return false;
            }
            for (Integer jt : beforeData.get(it).keySet()) {
                if (!afterData.get(it).containsKey(jt)) {
                    return false;
                }
                if (!Objects.equals(afterData.get(it).get(jt), beforeData.get(it).get(jt))) {
                    return false;
                }
            }
        }
        return true;
    }
}
