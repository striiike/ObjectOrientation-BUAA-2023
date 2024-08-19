import com.oocourse.elevator2.ElevatorRequest;
import com.oocourse.elevator2.MaintainRequest;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.util.ArrayList;

public class Scheduler extends Thread {

    private int numOfElevator;
    private RequestQueue waitQueue;
    private boolean isClosed;

    private ArrayList<Elevator> elevators;

    public Scheduler(RequestQueue queue) {
        waitQueue = queue;
        isClosed = false;
        elevators = new ArrayList<>();

        numOfElevator = 6;
        for (int i = 1; i <= numOfElevator; i++) {
            Elevator elevator = new Elevator(i, this, this.waitQueue);
            elevators.add(elevator);
            elevator.start();
        }

    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
        // necessary
        synchronized (waitQueue) {
            waitQueue.notifyAll();
        }
        synchronized (elevators) {
            for (Elevator elevator : elevators) {
                if (elevator.isMaintaining()) {
                    continue;
                }
                synchronized (elevator.getRequestsOutside()) {
                    elevator.getRequestsOutside().notifyAll();
                }
            }
        }

    }

    public synchronized void addRequest(
            Request request) throws InterruptedException {
        // requestMap.get(personRequest.getFromFloor()).add(personRequest);


        // first scheme
        /*
        double max = 2147483640;

        double[] timeTable = new double[]{0, 0, 0, 0, 0, 0};
        for (int i = 0; i < numOfElevator; i++) {
            ShadowElevator shadow = new ShadowElevator(elevators.get(i));
            shadow.start();
            shadow.join();
            timeTable[i] = shadow.getTime();
        }

        Elevator elevatorToAssign = new Elevator();
        for (int i = 0; i < numOfElevator; i++) {
            ShadowElevator shadow = new ShadowElevator(elevators.get(i));
            shadow.getRequestsOutside().add(personRequest);
            // System.out.println("TEST: shadow direction " + shadow.getDirection());
            // System.out.println("TEST: normal direction " + elevator.getDirection());
            shadow.start();
            shadow.join();

            double min = 0;
            for (int j = 0; j < numOfElevator; j++) {
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
        */
        /* mine */
        /*
        Elevator elevatorToAssign = new Elevator();
        for (int i = 0; i < numOfElevator; i++) {
            ShadowElevator shadow = new ShadowElevator(elevators.get(i));
            shadow.getRequestsOutside().add(personRequest);
            // System.out.println("TEST: shadow direction " + shadow.getDirection());
            // System.out.println("TEST: normal direction " + elevator.getDirection());
            shadow.start();
            shadow.join();


            if (shadow.getTime() < max) {
                elevatorToAssign = elevators.get(i);
                max = shadow.getTime();
            }
            // System.out.println(
            // "TEST: <time:" + shadow.getTime() + "> <id: " + elevator.getElevatorId() + ">");

        }
        */

        // elevatorToAssign = elevators.get(0);
        /*
        synchronized (elevatorToAssign.getRequestsOutside()) {
            elevatorToAssign.addPersonRequest(personRequest);
        }
        */

        // System.out.println(
        // "TEST: <time:" + max + "> <req: " + personRequest + ">");

        notifyAll();
    }

    public boolean isEmpty() {
        return waitQueue.isEmpty();
    }

    public boolean allFinished() {
        for (Elevator elevator : elevators) {
            if (!elevator.isClosed() && (!elevator.getRequestsInside().isEmpty()
                    || !elevator.getRequestsOutside().isEmpty())) {

                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        while (true) {

            // System.out.println(waitQueue.isEmpty() + " " + isClosed + " " + allFinished());
            if (waitQueue.isEmpty() && isClosed && allFinished()) {
                // System.out.println("scheduler bye bye");
                for (Elevator elevator : elevators) {
                    synchronized (elevator.getRequestsOutside()) {
                        elevator.getRequestsOutside().notifyAll();
                    }
                }

                return;
            }

            if (waitQueue.isEmpty()) {
                synchronized (waitQueue) {
                    try {
                        waitQueue.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            ArrayList<Request> queue = waitQueue.clone();
            for (int k = 0; k < queue.size(); k++) {
                if (queue.get(k) instanceof ElevatorRequest) {
                    //ElevatorRequest elevatorRequest = new ElevatorRequest()
                    synchronized (elevators) {
                        for (Elevator elevator : elevators) {
                            elevator.setRebuild(true);
                        }
                    }
                    numOfElevator++;
                    Elevator elevator = new Elevator(
                            ((ElevatorRequest) queue.get(k)).getElevatorId(),
                            this, this.waitQueue);
                    elevator.setCapacity(((ElevatorRequest) queue.get(k)).getCapacity());
                    elevator.setFloor(((ElevatorRequest) queue.get(k)).getFloor());
                    elevator.setSpeed((int) (((ElevatorRequest) queue.get(k)).getSpeed() * 1000));
                    elevator.start();
                    synchronized (elevators) {
                        elevators.add(elevator);
                    }

                }
                if (queue.get(k) instanceof MaintainRequest) {
                    synchronized (elevators) {
                        for (Elevator elevator : elevators) {
                            if (elevator.getElevatorId()
                                    == ((MaintainRequest) queue.get(k)).getElevatorId()) {
                                elevator.setMaintaining(true);
                            }
                        }
                    }
                }
            }
            for (int k = 0; k < queue.size(); k++) {
                if (queue.get(k) instanceof PersonRequest) {

                    PersonRequest personRequest = (PersonRequest) queue.get(k);
                    dispatch(personRequest);
                    // System.out.println(personRequest.getPersonId()+" thrown");


                }



            }


        }

    }

    public void dispatch(PersonRequest personRequest) {
        double max = 2147483640;
        double[] timeTable = new double[elevators.size()];

        do {
            for (int i = 0; i < numOfElevator; i++) {
                if (elevators.get(i).isMaintaining()) {
                    continue;
                }
                ShadowElevator shadow = new ShadowElevator(elevators.get(i));
                shadow.start();
                try {
                    shadow.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                timeTable[i] = shadow.getTime();
            }
        } while (false);

        Elevator elevatorToAssign = new Elevator();
        for (int i = 0; i < numOfElevator; i++) {
            if (elevators.get(i).isMaintaining()) {
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
                if (elevators.get(j).isMaintaining()) {
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


        synchronized (elevatorToAssign.getRequestsOutside()) {
            elevatorToAssign.addPersonRequest(personRequest);
        }
    }

}
