import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RequestQueue {
    private final Lock locker = new ReentrantLock();
    private ArrayList<Request> queue;

    public RequestQueue() {
        queue = new ArrayList<>();
    }

    public synchronized ArrayList<Request> getQueue() {
        return queue;
    }

    public synchronized void setQueue(ArrayList<Request> queue) {
        this.queue = queue;
    }

    public synchronized void addRequest(Request request) {
        queue.add(request);
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public synchronized ArrayList<Request> clone() {

        ArrayList<Request> clone = new ArrayList<>();

        locker.lock();
        try {
            for (Request item : queue) {
                if (item instanceof PersonRequest) {

                    PersonRequest clone1 = new PersonRequest(
                            ((PersonRequest) item).getFromFloor(),
                            ((PersonRequest) item).getToFloor(),
                            ((PersonRequest) item).getPersonId());
                    clone.add(clone1);
                }
                if (item instanceof ElevatorRequest) {
                    ElevatorRequest clone1 = new ElevatorRequest(
                            ((ElevatorRequest) item).getElevatorId(),
                            ((ElevatorRequest) item).getFloor(),
                            ((ElevatorRequest) item).getCapacity(),
                            ((ElevatorRequest) item).getSpeed());
                    clone.add(clone1);
                }
                if (item instanceof MaintainRequest) {
                    MaintainRequest clone1 = new MaintainRequest(
                            ((MaintainRequest) item).getElevatorId());
                    clone.add(clone1);
                }
            }
            queue.clear();
        } finally {
            locker.unlock();
        }


        // System.out.println("clone done");
        return clone;
    }

}
