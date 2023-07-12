import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import exception.MyAcquaintanceNotFoundException;
import exception.MyEmojiIdNotFoundException;
import exception.MyEqualEmojiIdException;
import exception.MyEqualGroupIdException;
import exception.MyEqualMessageIdException;
import exception.MyEqualPersonIdException;
import exception.MyEqualRelationException;
import exception.MyGroupIdNotFoundException;
import exception.MyMessageIdNotFoundException;
import exception.MyPathNotFoundException;
import exception.MyPersonIdNotFoundException;
import exception.MyRelationNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyNetwork implements Network {

    private final Dsu dsu;
    private final HashMap<Integer, Person> peopleMap;
    private int blockSum = 0;
    private int tripleSum = 0;
    private int coupleSum = 0;
    private final HashMap<Integer, Group> groupsMap;
    private final HashMap<Integer, Message> messagesMap;
    private final HashMap<Integer, Integer> emojiHeatMap;

    public MyNetwork() {
        this.dsu = new Dsu();
        this.peopleMap = new HashMap<>();
        this.groupsMap = new HashMap<>();
        this.messagesMap = new HashMap<>();
        this.emojiHeatMap = new HashMap<>();
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
        ((MyPerson) person).setDsu();
        dsu.putPerson(person.getId());
        blockSum++;
    }

    public boolean hasCouple(int id) {
        MyPerson person = ((MyPerson) getPerson(id));
        if (person.queryBest() == null) {
            return false;
        }
        return person.getId() == ((MyPerson) person.queryBest()).queryBest().getId();
    }

    public void updateCoupleSum(boolean ex1, boolean ex2,
                                MyPerson person1, MyPerson person2) {
        if (ex1 && !hasCouple(person1.getId())) {
            coupleSum--;
        }
        if (ex2 && !hasCouple(person2.getId())) {
            coupleSum--;
        }
        if (!ex1 && hasCouple(person1.getId())) {
            coupleSum++;
        }
        if (!ex2 && hasCouple(person2.getId())) {
            coupleSum++;
        }
        if (person1.getId() == person2.queryBest().getId()
                && person2.getId() == person1.queryBest().getId()) {
            coupleSum--;
        }
    }

    public ArrayList<Integer> getSameGroups(MyPerson person1, MyPerson person2) {
        ArrayList<Integer> groups = new ArrayList<>();
        for (Integer i : person1.getGroups()) {
            if (person2.getGroups().contains(i)) {
                groups.add(i);
            }
        }
        return groups;
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
        boolean ex1 = hasCouple(id1);
        boolean ex2 = hasCouple(id2);
        person1.addAcquaintance(person2, value);
        person2.addAcquaintance(person1, value);
        // couple
        updateCoupleSum(ex1, ex2, person1, person2);
        if (dsu.union(id1, id2, false)) {
            blockSum--;
        }
        // group
        ArrayList<Integer> sameGroups = getSameGroups(person1, person2);
        for (Integer i : sameGroups) {
            ((MyGroup) groupsMap.get(i)).addValue(value * 2);
        }
        MyPerson forSum1;
        MyPerson forSum2;
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
    public void modifyRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualPersonIdException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (contains(id1) && !contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (contains(id1) && contains(id2) && id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        }
        if (contains(id1) && contains(id2) && id1 != id2
                && !getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        boolean delete = person1.queryValue(person2) + value <= 0;
        int valueToAdd = value + person1.queryValue(person2);
        if (delete) {
            // group
            ArrayList<Integer> sameGroups = getSameGroups(person1, person2);
            for (Integer i : sameGroups) {
                ((MyGroup) groupsMap.get(i)).addValue(-person1.queryValue(person2) * 2);
            }
        }
        // couple
        if (person1.getId() == person2.queryBest().getId()
                && person2.getId() == person1.queryBest().getId()) {
            coupleSum--;
        }
        // goddamn checkstyle
        boolean bestBroke1 = person2.getId() == person1.queryBest().getId();
        person1.removeAcquaintance(person2);
        boolean bestBroke2 = person1.getId() == person2.queryBest().getId();
        person2.removeAcquaintance(person1);
        // couple
        if (hasCouple(person1.getId()) && bestBroke1) {
            coupleSum++;
        }
        if (hasCouple(person2.getId()) && bestBroke2) {
            coupleSum++;
        }
        // it is actually merging
        if (!delete) {
            boolean ex1 = hasCouple(id1);
            boolean ex2 = hasCouple(id2);
            person1.addAcquaintance(person2, valueToAdd);
            person2.addAcquaintance(person1, valueToAdd);
            // couple
            updateCoupleSum(ex1, ex2, person1, person2);
            // group
            ArrayList<Integer> sameGroups = getSameGroups(person1, person2);
            for (Integer i : sameGroups) {
                ((MyGroup) groupsMap.get(i)).addValue(value * 2);
            }
        } else {
            if (dsu.deleteEdge(id1, id2)) {
                blockSum++;
            }
            // tri
            ArrayList<Person> temp = new ArrayList<>(person1.getAcquaintanceMap().keySet());
            for (Person person : temp) {
                if (((MyPerson) person).getAcquaintanceMap().containsKey(person2)) {
                    tripleSum--;
                }
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
        return dsu.isCircle(id1, id2);
    }

    @Override
    public int queryBlockSum() {
        return blockSum;
    }

    @Override
    public int queryTripleSum() {
        return tripleSum;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        if (groupsMap.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());
        }
        this.groupsMap.put(group.getId(), group);
    }

    @Override
    public Group getGroup(int id) {
        return groupsMap.get(id);
    }

    @Override
    public void addToGroup(int id1, int id2) throws
            GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (!groupsMap.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        }
        if (groupsMap.containsKey(id2) && !peopleMap.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (groupsMap.containsKey(id2) && peopleMap.containsKey(id1)
                && getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        if (getGroup(id2).getSize() > 1111) {
            return;
        }
        getGroup(id2).addPerson(getPerson(id1));
        ((MyPerson) getPerson(id1)).getGroups().add(id2);
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groupsMap.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return getGroup(id).getValueSum();
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groupsMap.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id);
        }
        return getGroup(id).getAgeVar();
    }

    @Override
    public void delFromGroup(int id1, int id2) throws
            GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (!groupsMap.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);
        }
        if (groupsMap.containsKey(id2) && !peopleMap.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (groupsMap.containsKey(id2) && peopleMap.containsKey(id1)
                && !getGroup(id2).hasPerson(getPerson(id1))) {
            throw new MyEqualPersonIdException(id1);
        }
        getGroup(id2).delPerson(getPerson(id1));
        ((MyPerson) getPerson(id1)).getGroups().remove((Integer) id2);
    }

    @Override
    public boolean containsMessage(int id) {
        return messagesMap.containsKey(id);
    }

    @Override
    public void addMessage(Message message)
            throws EqualMessageIdException, EqualPersonIdException, EmojiIdNotFoundException {
        if (messagesMap.containsKey(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message instanceof EmojiMessage
                && !emojiHeatMap.containsKey(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
        }
        if (!messagesMap.containsKey(message.getId()) && message.getType() == 0
                && message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messagesMap.put(message.getId(), message);
    }

    @Override
    public Message getMessage(int id) {
        return messagesMap.get(id);
    }

    @Override
    public void sendMessage(int id) throws
            RelationNotFoundException, MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        }
        if (containsMessage(id) && getMessage(id).getType() == 0
                && !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
            throw new MyRelationNotFoundException(
                    getMessage(id).getPerson1().getId(),
                    getMessage(id).getPerson2().getId());
        }
        if (containsMessage(id) && getMessage(id).getType() == 1 &&
                !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
            throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
        }

        int socialValue = getMessage(id).getSocialValue();
        Message message = getMessage(id);
        if (message instanceof EmojiMessage) {
            emojiHeatMap.merge(((EmojiMessage) message).getEmojiId(), 1, Integer::sum);
        }

        if (message.getType() == 0) {
            if ((message instanceof RedEnvelopeMessage)) {
                message.getPerson1().addMoney(-((RedEnvelopeMessage) message).getMoney());
                message.getPerson2().addMoney(((RedEnvelopeMessage) message).getMoney());
            }
            message.getPerson1().addSocialValue(socialValue);
            message.getPerson2().addSocialValue(socialValue);
            message.getPerson2().getMessages().add(0, message);
        }

        if (getMessage(id).getType() == 1) {
            if ((message) instanceof RedEnvelopeMessage) {
                ((MyGroup) message.getGroup()).receiveRedEnvelope(message);
            }
            ((MyGroup) message.getGroup()).receiveMessage(socialValue);
        }
        messagesMap.remove(id);
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!peopleMap.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!peopleMap.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getReceivedMessages();
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiHeatMap.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (emojiHeatMap.containsKey(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiHeatMap.put(id, 0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!emojiHeatMap.containsKey(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiHeatMap.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        emojiHeatMap.entrySet().removeIf(entry -> entry.getValue() < limit);
        messagesMap.entrySet().removeIf(entry -> entry.getValue() instanceof EmojiMessage
                && !containsEmojiId(((EmojiMessage) entry.getValue()).getEmojiId()));
        return emojiHeatMap.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        ((MyPerson) getPerson(personId)).clearNotice();
    }

    @Override
    public int queryBestAcquaintance(int id)
            throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!peopleMap.containsKey(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        if (peopleMap.containsKey(id)
                && ((MyPerson) getPerson(id)).getAcquaintanceMap().size() == 0) {
            throw new MyAcquaintanceNotFoundException(id);
        }
        return ((MyPerson) getPerson(id)).queryBest().getId();
    }

    @Override
    public int queryCoupleSum() {
        return coupleSum;
    }

    @Override
    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        int ret = dsu.queryLeastMoments(id, peopleMap);
        if (ret > 0) {
            return ret;
        } else {
            throw new MyPathNotFoundException(id);
        }
    }

    @Override
    public int deleteColdEmojiOKTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        return OkTest.check(limit, beforeData, afterData, result);
    }
}
