import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;

import java.util.HashMap;

public class MyGroup implements Group {

    private final int id;
    private int valueSum;
    private int ageSum;
    private int ageMean;
    private int ageVar;
    private int ageSquare;
    private final HashMap<Integer, Person> peopleMap;

    public MyGroup(int id) {
        this.id = id;
        this.peopleMap = new HashMap<>();
        valueSum = 0;
        ageSquare = 0;
        ageSum = 0;
        ageMean = 0;
        ageVar = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void addPerson(Person person) {
        for (Person personInGroup : peopleMap.values()) {
            valueSum += person.queryValue(personInGroup) * 2;
        }
        peopleMap.put(person.getId(), person);
        ageSum += person.getAge();
        ageSquare += person.getAge() * person.getAge();
        ageMean = ageSum / getSize();

        ageVar = (ageSquare - 2 * ageMean * ageSum + getSize() * ageMean * ageMean) / getSize();

    }

    @Override
    public boolean hasPerson(Person person) {
        return peopleMap.containsValue(person);
    }

    @Override
    public int getValueSum() {
        return this.valueSum;
    }

    @Override
    public int getAgeMean() {
        return this.ageMean;
    }

    @Override
    public int getAgeVar() {
        return this.ageVar;
    }

    @Override
    public void delPerson(Person person) {
        for (Person personInGroup : peopleMap.values()) {
            valueSum -= person.queryValue(personInGroup) * 2;
        }
        peopleMap.remove(person.getId());
        if (getSize() == 0) {
            ageSum = 0;
            ageSquare = 0;
            ageMean = 0;
            ageVar = 0;
            return;
        }
        ageSum -= person.getAge();
        ageSquare -= person.getAge() * person.getAge();
        ageMean = ageSum / getSize();
        ageVar = (ageSquare - 2 * ageMean * ageSum + getSize() * ageMean * ageMean) / getSize();

    }

    @Override
    public int getSize() {
        return peopleMap.size();
    }

    public void receiveMessage(int socialValue) {
        for (Person person : peopleMap.values()) {
            person.addSocialValue(socialValue);
        }
    }

    public void addValue(int num) {
        valueSum += num;
    }

    public void receiveRedEnvelope(Message message) {
        if (message instanceof RedEnvelopeMessage) {
            int i = ((RedEnvelopeMessage) message).getMoney() / getSize();
            message.getPerson1().addMoney(-i * (getSize() - 1));
            for (Person personInGroup : peopleMap.values()) {
                if (personInGroup.getId() == message.getPerson1().getId()) {
                    continue;
                }
                personInGroup.addMoney(i);
            }
        }
    }
}
