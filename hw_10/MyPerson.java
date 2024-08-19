import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private int socialValue;
    private final ArrayList<Message> messages;
    private final HashMap<Person, Integer> acquaintanceMap;
    private Person bestAcquaintance;
    private final ArrayList<Integer> groups;
    private int money;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintanceMap = new HashMap<>();
        this.messages = new ArrayList<>();
        this.socialValue = 0;
        this.bestAcquaintance = null;
        this.groups = new ArrayList<>();
        this.money = 0;
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

    @Override
    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        List<Message> messageList = new ArrayList<>();
        for (int i = 0; i < messages.size() && i <= 4; i++) {
            messageList.add(messages.get(i));
        }
        return messageList;
    }

    @Override
    public void addMoney(int num) {
        this.money += num;
    }

    @Override
    public int getMoney() {
        return this.money;
    }

    public void addAcquaintance(Person person, int value) {
        this.getAcquaintanceMap().merge(person, value, Integer::sum);
        if (bestAcquaintance == null) {
            bestAcquaintance = person;
        }
        if ((acquaintanceMap.get(bestAcquaintance) == value
                && person.getId() < bestAcquaintance.getId())
                || acquaintanceMap.get(bestAcquaintance) < value) {
            bestAcquaintance = person;
        }
    }

    public void removeAcquaintance(Person person) {
        this.getAcquaintanceMap().remove(person);
        bestAcquaintance = null;
        for (Map.Entry<Person, Integer> entry : acquaintanceMap.entrySet()) {
            Person key = entry.getKey();
            int value = entry.getValue();
            if (bestAcquaintance == null) {
                bestAcquaintance = key;
            }
            if ((acquaintanceMap.get(bestAcquaintance) == value
                    && key.getId() < bestAcquaintance.getId())
                    || acquaintanceMap.get(bestAcquaintance) < value) {
                bestAcquaintance = key;
            }
        }
    }

    public Person queryBest() {
        return this.bestAcquaintance;
    }

    public void setDsu() {
    }

    public HashMap<Person, Integer> getAcquaintanceMap() {
        return this.acquaintanceMap;
    }

    public ArrayList<Integer> getGroups() {
        return groups;
    }

    public void clearNotice() {
        messages.removeIf(message -> message instanceof NoticeMessage);
    }

}
