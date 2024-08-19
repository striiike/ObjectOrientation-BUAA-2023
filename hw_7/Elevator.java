import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;
import java.util.Iterator;

public class Elevator extends Thread {
    private RequestQueue waitQueue;

    private int capacity;
    private int speed;
    private final int up = 1;
    private int id;
    private int floor;

    private ArrayList<PersonRequest> requestsInside;
    private Scheduler scheduler;
    private Tunnel tunnel;
    private int direction;
    private long lastTime;
    private int accessFloor;
    private Supervisor supervisor;

    public ArrayList<PersonRequest> getRequestsInside() {
        return requestsInside;
    }

    public Elevator() {
    }

    public Elevator(int id, Scheduler scheduler,
                    RequestQueue waitQueue, int accessFloor, Supervisor supervisor) {
        this.supervisor = supervisor;
        this.accessFloor = accessFloor;
        this.tunnel = new Tunnel();
        this.speed = 400;
        this.capacity = 6;
        this.scheduler = scheduler;
        this.id = id;
        this.floor = 1;
        this.requestsInside = new ArrayList<>();
        this.direction = up;
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
            // System.out.println("sleep for " + (this.speed - curTime + lastTime) + " " + id);
            sleep((int) (this.speed - curTime + lastTime));
        }
        // sleep(400);

        printInfo("ARRIVE");
        updateTime();
    }

    public void in() {
        synchronized (tunnel) {
            Iterator<PersonRequest> item = tunnel.getRequestOutside().iterator();
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
        synchronized (requestsInside) {
            Iterator<PersonRequest> item = requestsInside.iterator();
            while (item.hasNext()) {
                PersonRequest personRequest = item.next();
                if (personRequest.getToFloor() == floor) {
                    printInfo("OUT", personRequest);

                    // if it is transfer option
                    if (personRequest instanceof TransferRequest) {
                        if (((TransferRequest) personRequest).getFinalToFloor()
                                != personRequest.getToFloor()) {
                            waitQueue.addRequest(new TransferRequest(
                                    floor,
                                    ((TransferRequest) personRequest).getFinalToFloor(),
                                    personRequest.getPersonId(),
                                    ((TransferRequest) personRequest).getFinalToFloor()
                            ));
                            synchronized (waitQueue) {
                                waitQueue.notifyAll();
                            }
                        }
                    }


                    item.remove();
                }
            }
        }
    }

    // 1 for dump , 2 for only out
    public int needToOpen() {
        for (PersonRequest personRequest : requestsInside) {
            if (personRequest.getToFloor() == floor) {
                return 1;
            }
        }
        if (requestsInside.size() == this.capacity) {
            return 0;
        }


        synchronized (tunnel) {
            for (PersonRequest personRequest : tunnel.getRequestOutside()) {
                if (personRequest.getFromFloor() == floor
                        && (personRequest.getToFloor() - floor) * direction > 0) {
                    return 2;
                }
            }
        }
        return 0;
    }

    public boolean hasSameDirection() {
        synchronized (tunnel) {
            for (PersonRequest personRequest : tunnel.getRequestOutside()) {
                // if ((personRequest.getFromFloor() - floor) *
                //        (personRequest.getToFloor() - personRequest.getFromFloor()) > 0) {
                //    return true;
                // }
                if ((personRequest.getFromFloor() - floor) * direction > 0) {
                    // System.out.println("TEST: which? " + personRequest);
                    return true;
                }
            }
        }
        return false;
    }

    public void allOut(ArrayList<PersonRequest> array, boolean isOutside) {

        for (PersonRequest personRequest : array) {

            PersonRequest newRequest;
            if (!isOutside) {
                if (personRequest instanceof TransferRequest) {
                    newRequest = new TransferRequest(
                            this.floor,
                            ((TransferRequest) personRequest).getFinalToFloor(),
                            personRequest.getPersonId(),
                            ((TransferRequest) personRequest).getFinalToFloor()
                    );
                } else {
                    newRequest = new PersonRequest(
                            this.floor, personRequest.getToFloor(), personRequest.getPersonId());
                }

                printInfo("OUT", personRequest);
                waitQueue.addRequest(newRequest);
                continue;
            }
            if (personRequest instanceof TransferRequest) {
                newRequest = new TransferRequest(
                        personRequest.getFromFloor(),
                        ((TransferRequest) personRequest).getFinalToFloor(),
                        personRequest.getPersonId(),
                        ((TransferRequest) personRequest).getFinalToFloor()
                );
            } else {
                newRequest = personRequest;
            }
            waitQueue.addRequest(newRequest);

        }


    }

    // needs fixing
    public void maintain() throws InterruptedException {
        if (requestsInside.size() == 0) {
            /*for (PersonRequest personRequest : tunnel.getRequestOutside()) {
                waitQueue.addRequest(personRequest);
            }*/
            allOut(tunnel.getRequestOutside(), true);

            // System.out.println("scheduler wake up " + waitQueue.isEmpty());
            printInfo("MAINTAIN_ABLE", this.id);
            tunnel.setMaintained(true);
            synchronized (waitQueue) {
                waitQueue.notifyAll();
            }
            return;
        }

        supervisor.acquirePickup(this);


        printInfo("OPEN");
        if (this.needToOpen() > 0) {
            out();
        }


        /*for (PersonRequest personRequest : requestsInside) {
            PersonRequest newRequest = new PersonRequest(
                    this.floor, personRequest.getToFloor(), personRequest.getPersonId());
            waitQueue.addRequest(newRequest);
            printInfo("OUT", personRequest);
        }*/
        allOut(requestsInside, false);
        requestsInside.clear();
        allOut(tunnel.getRequestOutside(), true);
        /*for (PersonRequest personRequest : tunnel.getRequestOutside()) {
            waitQueue.addRequest(personRequest);
        }*/


        sleep(400);
        printInfo("CLOSE");

        supervisor.releasePickup(this);

        printInfo("MAINTAIN_ABLE", this.id);
        tunnel.setMaintained(true);
        synchronized (waitQueue) {
            waitQueue.notifyAll();
        }
    }

    public void visorOnOpen() {
        int signal;
        while ((signal = this.needToOpen()) > 0) {
            if (signal == 2) {
                try {
                    supervisor.acquirePickup(this);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    supervisor.acquireAccess(this);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            printInfo("OPEN");
            sleep(400);
            // System.out.println("start out " + id);
            out();
            // System.out.println("STart in " + id);
            in();
            printInfo("CLOSE");

            if (signal == 2) {
                supervisor.releasePickup(this);
            } else {
                supervisor.releaseAccess(this);
            }

            updateTime();
        }
    }

    @Override
    public void run() {
        while (true) {

            // System.out.println("DEBUG: !!");

            // System.out.println("start notify scheduler " + id);
            synchronized (waitQueue) {
                if (scheduler.isClosing()) {
                    // System.out.println(scheduler.isClosing() + " " + id);
                    waitQueue.notifyAll();
                    // System.out.println("notify scheduler " + id);
                }
            }

            // System.out.println("start judge close " + id);
            synchronized (tunnel) {
                if (tunnel.isClosed()) {
                    // System.out.println("All finished? " + scheduler.allFinished());
                    // System.out.println("elevator bye bye" + getElevatorId());
                    return;
                }

                // System.out.println("start judge maintaining " + id);
                // scheduler.nextPersonRequests(this);
                if (tunnel.isMaintaining()) {
                    try {
                        maintain();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // System.out.println("elevator bye bye");
                    // System.out.println("elevator bye bye" + getElevatorId());
                    return;
                }
                if (requestsInside.isEmpty() && tunnel.getRequestOutside().isEmpty()) {
                    try {
                        tunnel.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            visorOnOpen();
            if (!requestsInside.isEmpty()) {
                move();
            } else {
                if (tunnel.getRequestOutside().isEmpty()) {
                    continue;
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
        synchronized (tunnel) {
            this.tunnel.getRequestOutside().add(personRequest);
            tunnel.notifyAll();
            // System.out.println("notify " + id);
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

        shadow = new ShadowElevator(this);

        return shadow;
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

    public Tunnel getTunnel() {
        return this.tunnel;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public int getAccessFloor() {
        return accessFloor;
    }
}
