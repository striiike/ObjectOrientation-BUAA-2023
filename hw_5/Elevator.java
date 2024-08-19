import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator extends Thread {
    private Lock locker = new ReentrantLock();
    private boolean isMaintaining;
    private RequestQueue waitQueue;
    private boolean rebuild;

    private boolean isClosed;
    private int capacity;
    private int speed;
    private final int up = 1;
    private final int down = -1;
    private int id;
    private int floor;

    private ArrayList<PersonRequest> requestsInside;
    private ArrayList<PersonRequest> requestsOutside;
    private Scheduler scheduler;
    private int direction;
    private long lastTime;

    public ArrayList<PersonRequest> getRequestsInside() {
        return requestsInside;
    }

    public void setRequestsInside(ArrayList<PersonRequest> requestsInside) {
        this.requestsInside = requestsInside;
    }

    public synchronized ArrayList<PersonRequest> getRequestsOutside() {
        return requestsOutside;
    }

    public void setRequestsOutside(ArrayList<PersonRequest> requestsOutside) {
        this.requestsOutside = requestsOutside;
    }

    public Elevator() {
    }

    public Elevator(int id, Scheduler scheduler, RequestQueue waitQueue) {
        this.rebuild = false;
        this.isClosed = false;
        this.speed = 400;
        this.capacity = 6;
        this.scheduler = scheduler;
        this.id = id;
        this.floor = 1;
        this.requestsInside = new ArrayList<>();
        this.requestsOutside = new ArrayList<>();
        this.direction = up;
        this.isMaintaining = false;
        lastTime = System.currentTimeMillis();
        this.waitQueue = waitQueue;
    }

    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void printInfo(String operand) {
        TimableOutput.println(operand + "-" + floor + "-" + id);
    }

    public void printInfo(String operand, PersonRequest personRequest) {
        TimableOutput.println(operand +
                "-" + personRequest.getPersonId() + "-" + floor + "-" + id);
    }

    public void printInfo(String operand, int id) {
        TimableOutput.println(operand +
                "-" + id);
    }

    public void updateTime() {
        this.lastTime = System.currentTimeMillis();
    }

    public void move() {

        if (direction == up) {
            floor++;
        } else {
            floor--;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - lastTime < this.speed) {
            sleep((int) (this.speed - curTime + lastTime));
        }
        // sleep(400);

        printInfo("ARRIVE");
        updateTime();
    }

    public void in() {
        synchronized (requestsOutside) {
            Iterator<PersonRequest> item = requestsOutside.iterator();
            while (item.hasNext()) {
                PersonRequest personRequest = item.next();
                if (personRequest.getFromFloor() == floor
                        && (personRequest.getToFloor() - floor) * direction > 0
                        && (requestsInside.size() < this.capacity)) {
                    printInfo("IN", personRequest);
                    synchronized (requestsInside) {
                        requestsInside.add(personRequest);
                    }
                    item.remove();
                }
            }
        }
    }

    public void out() {
        Iterator<PersonRequest> item = requestsInside.iterator();
        while (item.hasNext()) {
            PersonRequest personRequest = item.next();
            if (personRequest.getToFloor() == floor) {
                printInfo("OUT", personRequest);
                item.remove();
            }
        }
    }

    public boolean needToOpen() {
        for (PersonRequest personRequest : requestsInside) {
            if (personRequest.getToFloor() == floor) {
                return true;
            }
        }
        if (requestsInside.size() == this.capacity) {
            return false;
        }
        for (PersonRequest personRequest : requestsOutside) {
            if (personRequest.getFromFloor() == floor
                    && (personRequest.getToFloor() - floor) * direction > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSameDirection() {
        for (PersonRequest personRequest : requestsOutside) {
            // if ((personRequest.getFromFloor() - floor) *
            //        (personRequest.getToFloor() - personRequest.getFromFloor()) > 0) {
            //    return true;
            // }
            if ((personRequest.getFromFloor() - floor) * direction > 0) {
                // System.out.println("TEST: which? " + personRequest);
                return true;
            }
        }
        return false;
    }

    public void maintain() {
        if (requestsInside.size() == 0) {
            for (PersonRequest personRequest : requestsOutside) {
                waitQueue.addRequest(personRequest);
            }

            synchronized (waitQueue) {
                waitQueue.notifyAll();
            }
            printInfo("MAINTAIN_ABLE", this.id);
            isClosed = true;
            return;
        }

        printInfo("OPEN");
        if (this.needToOpen()) {
            out();
        }


        for (PersonRequest personRequest : requestsInside) {
            PersonRequest newRequest = new PersonRequest(
                    this.floor, personRequest.getToFloor(), personRequest.getPersonId());
            waitQueue.addRequest(newRequest);
            printInfo("OUT", personRequest);
        }
        requestsInside.clear();

        for (PersonRequest personRequest : requestsOutside) {
            waitQueue.addRequest(personRequest);
        }

        synchronized (waitQueue) {
            waitQueue.notifyAll();
        }
        sleep(400);
        printInfo("CLOSE");
        printInfo("MAINTAIN_ABLE", this.id);
        isClosed = true;

    }

    public void rebuild() {
        synchronized (requestsOutside) {
            for (PersonRequest personRequest : requestsOutside) {
                waitQueue.addRequest(personRequest);
            }
            requestsOutside.clear();
            this.rebuild = false;
        }
        synchronized (waitQueue) {
            waitQueue.notifyAll();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isMaintaining) {
                maintain();
                // System.out.println("elevator bye bye");
                return;
            }
            if (this.rebuild && !isMaintaining) {
                // rebuild();
            }
            // System.out.println("DEBUG: !!");
            if (requestsInside.isEmpty()
                    && requestsOutside.isEmpty()
                    && scheduler.isClosed()
                    && waitQueue.isEmpty()
                    && scheduler.allFinished()
                    && !isMaintaining) {
                // System.out.println("All finished? " + scheduler.allFinished());
                // System.out.println("elevator bye bye");
                return;
            }
            // scheduler.nextPersonRequests(this);
            if (isMaintaining) {
                maintain();
                // System.out.println("elevator bye bye");
                return;
            }

            while (this.needToOpen()) {
                printInfo("OPEN");
                sleep(400);
                out();
                in();
                printInfo("CLOSE");
                updateTime();
            }

            if (!requestsInside.isEmpty()) {
                move();
            } else {
                if (requestsOutside.isEmpty()) {
                    if (scheduler.isClosed() && scheduler.allFinished()) {
                        // System.out.println("elevator bye bye");
                        synchronized (waitQueue) {
                            waitQueue.notifyAll();
                        }
                        if (isMaintaining) {
                            printInfo("MAINTAIN_ABLE", this.id);
                        }
                        return;
                    } else {

                        synchronized (requestsOutside) {
                            try {
                                requestsOutside.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // scheduler.waitPersonRequests(this);
                    }
                }

                if (hasSameDirection()) {
                    move();
                } else {
                    direction = -direction;
                }
            }


        }

    }

    public void addPersonRequest(PersonRequest personRequest) {
        synchronized (requestsOutside) {
            this.requestsOutside.add(personRequest);
            requestsOutside.notifyAll();
        }
        // this.requestsOutside.add(personRequest);

        // System.out.println("TEST: add to " + id + " " + personRequest);
    }

    public int getFloor() {
        return this.floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getElevatorId() {
        return this.id;
    }

    public ShadowElevator getShadow() {
        ShadowElevator shadow;
        synchronized (this) {
            locker.lock();
            try {
                shadow = new ShadowElevator(this);
            } finally {
                locker.unlock();
            }
        }
        return shadow;
    }

    public boolean isMaintaining() {
        return isMaintaining;
    }

    public void setMaintaining(boolean maintaining) {
        synchronized (this.requestsOutside) {
            isMaintaining = maintaining;
            requestsOutside.notifyAll();
        }
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setRebuild(boolean rebuild) {
        this.rebuild = rebuild;
    }
}
