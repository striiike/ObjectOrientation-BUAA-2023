import com.oocourse.elevator2.PersonRequest;

import java.util.Iterator;

public class ShadowElevator extends Elevator {

    private int time;

    public ShadowElevator(int id) {
        super(0, null, null,2047, null);
        this.time = 0;
    }

    public ShadowElevator(Elevator elevator) {
        super(0, null, null,2047,null);
        this.setCapacity(elevator.getCapacity());
        this.setSpeed(elevator.getSpeed());
        this.time = 0;
        this.setFloor(elevator.getFloor());
        this.setDirection(elevator.getDirection());
        this.setTunnel(new Tunnel());
        synchronized (elevator.getRequestsInside()) {
            for (PersonRequest item : elevator.getRequestsInside()) {
                PersonRequest clone = new PersonRequest(item.getFromFloor(),
                        item.getToFloor(), item.getPersonId());
                this.getRequestsInside().add(clone);
            }
        }

        synchronized (elevator.getTunnel()) {
            for (PersonRequest item : elevator.getTunnel().getRequestOutside()) {
                PersonRequest clone = new PersonRequest(item.getFromFloor(),
                        item.getToFloor(), item.getPersonId());
                this.getTunnel().getRequestOutside().add(clone);
            }
        }
    }

    @Override
    public void sleep(int time) {
        this.time += time;
    }

    @Override
    public void printInfo(String operand) {
        /*
         TimableOutput.println(operand + "-" + this.getFloor() +
               "-" + this.getElevatorId() + " -- shadow");
        */
    }

    @Override
    public void printInfo(String operand, PersonRequest personRequest) {
        /*
         TimableOutput.println(operand +
             "-" + personRequest.getPersonId() +
             "-" + this.getFloor() +
             "-" + this.getElevatorId() + " -- shadow");
        */
    }

    @Override
    public void out() {
        synchronized (this.getRequestsInside()) {
            Iterator<PersonRequest> item = this.getRequestsInside().iterator();
            while (item.hasNext()) {
                PersonRequest personRequest = item.next();
                if (personRequest.getToFloor() == this.getFloor()) {
                    printInfo("OUT", personRequest);
                    item.remove();
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (this.getRequestsInside().isEmpty()
                    && this.getTunnel().getRequestOutside().isEmpty()) {
                return;
            }
            while (this.needToOpen() > 0) {
                sleep(400);
                out();
                in();
            }

            if (!this.getRequestsInside().isEmpty()) {
                // System.out.println(this.getRequestsInside().size()
                //         +" "+this.getRequestsOutside().size()+" -- shadow");
                move();
            } else {
                if (this.getTunnel().getRequestOutside().isEmpty()) {
                    return;
                }

                if (hasSameDirection()) {
                    move();
                } else {
                    this.setDirection(-this.getDirection());
                }
            }
        }
    }

    public int getTime() {
        return time;
    }

    @Override
    public void move() {
        if (this.getDirection() == 1) {
            this.setFloor(this.getFloor() + 1);
        } else {
            this.setFloor(this.getFloor() - 1);
        }
        sleep(super.getSpeed());

    }

    @Override
    public void addPersonRequest(PersonRequest personRequest) {
        this.getTunnel().getRequestOutside().add(personRequest);


    }

}
