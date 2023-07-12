import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

public class MyMessage implements Message {

    private final int id;
    private final int socialValue;
    private final int type;
    private final Person person1;
    private final Person person2;
    private final Group group;

    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Person messagePerson2) {
        this.type = 0;
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.group = null;
    }

    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Group messageGroup) {
        this.type = 1;
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.group = messageGroup;
        this.person2 = null;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public Person getPerson1() {
        return this.person1;
    }

    @Override
    public Person getPerson2() {
        return this.person2;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }
}
