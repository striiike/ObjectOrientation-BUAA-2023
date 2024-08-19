import com.oocourse.elevator1.PersonRequest;

import java.util.ArrayList;

public class Queue {
    private ArrayList<PersonRequest> queue;


    public synchronized ArrayList<PersonRequest> getQueue() {
        return queue;
    }

    public synchronized void setQueue(ArrayList<PersonRequest> queue) {
        this.queue = queue;
    }

    public synchronized void addRequest(PersonRequest personRequest){
        queue.add(personRequest);
    }

    public synchronized boolean isEmpty(){
        return queue.isEmpty();
    }


}
