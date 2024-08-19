import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.util.ArrayList;
import java.util.Random;

public class Scheduler extends Thread {

    private int numOfElevator;
    private final RequestQueue waitQueue;
    private boolean isClosing;

    private boolean isClosed;

    private final ArrayList<Elevator> elevators;

    private ArrayList<Tunnel> tunnels;

    private Supervisor supervisor;

    public Scheduler(RequestQueue queue, Supervisor supervisor) {
        this.supervisor = supervisor;
        waitQueue = queue;
        isClosing = false;
        isClosed = false;
        elevators = new ArrayList<>();
        tunnels = new ArrayList<>();

        numOfElevator = 6;
        for (int i = 1; i <= numOfElevator; i++) {
            Elevator elevator = new Elevator(
                    i, this, this.waitQueue, 2047, supervisor);
            elevators.add(elevator);
            tunnels.add(elevator.getTunnel());
            elevator.start();
        }

    }

    public boolean isClosing() {
        return isClosing;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosing(boolean closing) {
        isClosing = closing;
        // necessary
        synchronized (waitQueue) {
            waitQueue.notifyAll();
        }


    }

    public Tunnel getTunnelFromEle(Elevator elevator) {
        // getTunnelFromEle(elevator)
        return tunnels.get(elevators.indexOf(elevator));
    }

    public boolean allFinished() {
        for (Elevator elevator : elevators) {
            if (!getTunnelFromEle(elevator).isMaintained()
                    && (!elevator.getRequestsInside().isEmpty()
                    || !getTunnelFromEle(elevator).getRequestOutside().isEmpty())) {
                // System.out.println("It is not maintained" + elevator.getElevatorId());
                return false;
            }
            if (getTunnelFromEle(elevator).isMaintaining()
                    && !getTunnelFromEle(elevator).isMaintained()) {
                // System.out.println("It is " + elevator.getElevatorId());
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {

            // System.out.println(waitQueue.isEmpty() + " " + isClosed + " " + allFinished());
            if (waitQueue.isEmpty() && isClosing && allFinished()) {
                // System.out.println("scheduler bye bye");
                isClosed = true;

                for (Elevator elevator : elevators) {
                    getTunnelFromEle(elevator).setClosed(true);
                }

                return;
            }
            if (waitQueue.isEmpty()) {
                // System.out.println("scheduler wait");
                synchronized (waitQueue) {
                    try {
                        waitQueue.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // System.out.println("scheduler notified");
            }

            // TimableOutput.println("It is my turn " + waitQueue.getQueue().size());


            ArrayList<Request> queue = waitQueue.clone();


            for (int k = 0; k < queue.size(); k++) {
                if (queue.get(k) instanceof MaintainRequest) {
                    /*System.out.println
                    ( ((MaintainRequest) queue.get(k)).getElevatorId()+" maintain");*/
                    for (Elevator elevator : elevators) {
                        if (elevator.getElevatorId()
                                == ((MaintainRequest) queue.get(k)).getElevatorId()) {
                            getTunnelFromEle(elevator).setMaintaining(true);
                        }
                    }

                }
                if (queue.get(k) instanceof ElevatorRequest) {
                    numOfElevator++;
                    Elevator elevator = new Elevator(
                            ((ElevatorRequest) queue.get(k)).getElevatorId(),
                            this, this.waitQueue,
                            2047,
                            this.supervisor);
                    elevator.setCapacity(((ElevatorRequest) queue.get(k)).getCapacity());
                    elevator.setFloor(((ElevatorRequest) queue.get(k)).getFloor());
                    elevator.setSpeed((int) (((ElevatorRequest) queue.get(k)).getSpeed() * 1000));
                    tunnels.add(elevator.getTunnel());
                    elevator.start();

                    elevators.add(elevator);


                }

            }
            for (int k = 0; k < queue.size(); k++) {
                if (queue.get(k) instanceof PersonRequest) {

                    PersonRequest personRequest = (PersonRequest) queue.get(k);
                    // System.out.println(personRequest);
                    dispatch(personRequest);
                    // System.out.println(personRequest.getPersonId()+" thrown");


                }


            }
            // System.out.println("dispatch done !");


        }

    }

    public boolean floorAccessible(int floor, int access) {
        // System.out.println(floor + " " + access + " ");
        return (((1 << (floor - 1)) & access) != 0);


    }

    public boolean floorOverlap(int access, int access2) {
        return (access & access2) > 0;
    }

    public Elevator getElevatorFromId(int id) {
        for (Elevator elevator : this.elevators) {
            if (getTunnelFromEle(elevator).isMaintaining()) {
                continue;
            }
            if (elevator.getElevatorId() == id) {
                return elevator;
            }
        }
        return null;
    }

    // fixing !!!!!!
    public int pickRand(int access, int floor) {
        ArrayList<Integer> a = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            if (((1 << (i - 1)) & access) != 0) {
                a.add(i);
            }
        }
        return a.get(new Random().nextInt(a.size()));
    }

    public boolean oneShot(PersonRequest personRequest) {
        // System.out.println(personRequest);


        ArrayList<ArrayList<Integer>> queue = new ArrayList<>();
        for (Elevator elevator : elevators) {
            if (getTunnelFromEle(elevator).isMaintaining()) {
                continue;
            }
            if (floorAccessible(personRequest.getFromFloor(), elevator.getAccessFloor())
                    && floorAccessible(personRequest.getToFloor(), elevator.getAccessFloor())) {
                return true;
            }
            if (floorAccessible(personRequest.getFromFloor(), elevator.getAccessFloor())) {
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(elevator.getElevatorId());
                queue.add(temp);
            }
        }


        // System.out.println("not one shot! " + personRequest) ;

        // System.out.println("bfs喵 " + elevator.getElevatorId());


        // System.out.println(queue.size() + " " + queue.get(0).size());
        while (!queue.isEmpty()) {


            // System.out.println(queue);
            ArrayList<Integer> head = queue.get(0);
            Elevator elevator = getElevatorFromId(head.get(0));
            for (Elevator nextElevator : elevators) {
                if (getTunnelFromEle(nextElevator).isMaintaining()
                        || head.contains(nextElevator.getElevatorId())) {
                    continue;
                }
                // System.out.println("each " + head + " " + nextElevator.getElevatorId());
                // System.out.println((head.size() - 1) + " " + head.get(0));
                Elevator elevatorForAccess = getElevatorFromId(head.get(head.size() - 1));
                        /*System.out.println("access is " + elevatorForAccess.getElevatorId()
                        +  " next is " + nextElevator.getElevatorId());*/
                if (floorOverlap(elevatorForAccess.getAccessFloor(),
                        nextElevator.getAccessFloor())) {

                    ArrayList<Integer> newLine = new ArrayList<>(head);
                    newLine.add(nextElevator.getElevatorId());
                    queue.add(newLine);

                    if (floorAccessible(personRequest.getToFloor()
                            , nextElevator.getAccessFloor())) {
                        /*System.out.println("TEST : " + elevator.getElevatorId() + " "
                                + getElevatorFromId(newLine.get(1)).getElevatorId());
                        System.out.println("TEST : " + elevator.getAccessFloor() + " "
                                + getElevatorFromId(newLine.get(1)).getAccessFloor());
                        System.out.println(personRequest);*/
                        int floor = pickRand(elevator.getAccessFloor()
                                & getElevatorFromId(newLine.get(1)).getAccessFloor(), 0);
                        elevator.addPersonRequest(new TransferRequest(
                                personRequest.getFromFloor(), floor,
                                personRequest.getPersonId(), personRequest.getToFloor()));
                        return false;
                    }
                }

            }
            queue.remove(0);
        }

        return true;
    }

    public boolean notQualified(Elevator elevator, PersonRequest personRequest) {
        return getTunnelFromEle(elevator).isMaintaining()
                || !floorAccessible(personRequest.getFromFloor(), elevator.getAccessFloor())
                || !floorAccessible(personRequest.getToFloor(), elevator.getAccessFloor());
    }

    public void dispatch(PersonRequest personRequest) {

        Elevator elevatorToAssign = new Elevator();
        // if need transfer, assign directly
        if (!oneShot(personRequest)) {
            return;
        }

        // System.out.println("is one shot " + personRequest);


        double max = 2147483640;
        double[] timeTable = new double[elevators.size()];

        for (int i = 0; i < numOfElevator; i++) {
            if (notQualified(elevators.get(i), personRequest)) {
                continue;
            }
            ShadowElevator shadow = elevators.get(i).getShadow();
            shadow.start();
            try {
                shadow.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            timeTable[i] = shadow.getTime();
        }

        for (int i = 0; i < numOfElevator; i++) {
            if (notQualified(elevators.get(i), personRequest)) {
                continue;
            }
            ShadowElevator shadow = elevators.get(i).getShadow();
            shadow.addPersonRequest(personRequest);
            // System.out.println("TEST: shadow direction " + shadow.getDirection());
            // System.out.println("TEST: normal direction " + elevator.getDirection());
            shadow.start();
            try {
                shadow.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            double min = 0;
            for (int j = 0; j < numOfElevator; j++) {
                if (notQualified(elevators.get(i), personRequest)) {
                    continue;
                }
                if (j != i) {
                    if (timeTable[j] > min) {
                        min = timeTable[j];
                    }
                } else {
                    if (shadow.getTime() > min) {
                        min = shadow.getTime();
                    }
                }
            }

            if (min < max) {
                elevatorToAssign = elevators.get(i);
                max = min;
            }
            // System.out.println(
            // "TEST: <time:" + shadow.getTime() + "> <id: " + elevator.getElevatorId() + ">");

        }
        elevatorToAssign.addPersonRequest(personRequest);
    }

}
