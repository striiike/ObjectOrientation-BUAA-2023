import com.oocourse.spec1.main.Person;

import java.util.HashMap;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private Person[] acquaintance;
    private int[] value;

    private HashMap<Person, Integer> acquaintanceMap;

    private Dsu dsu;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintanceMap = new HashMap<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return (((Person) obj).getId() == this.id);
        }
        return false;
    }

    @Override
    public boolean isLinked(Person person) {
        return person.getId() == id || acquaintanceMap.containsKey(person);
    }

    @Override
    public int queryValue(Person person) {
        if (!acquaintanceMap.containsKey(person)) {
            return 0;
        }
        return acquaintanceMap.get(person);
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void setDsu(Dsu dsu) {
        this.dsu = dsu;
    }

    public Person[] getAcquaintance() {
        return acquaintance;
    }

    public int[] getValue() {
        return value;
    }

    public HashMap<Person, Integer> getAcquaintanceMap() {
        return this.acquaintanceMap;
    }
}
