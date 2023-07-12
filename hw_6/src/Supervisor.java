import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Supervisor {
    private final ArrayList<Semaphore> pickup;
    private final ArrayList<Semaphore> service;

    public Supervisor() {
        pickup = new ArrayList<>();
        service = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Semaphore item = new Semaphore(4);
            Semaphore j = new Semaphore(2);
            service.add(item);
            pickup.add(j);
        }
    }

    public void acquireAccess(Elevator elevator) throws InterruptedException {
        service.get(elevator.getFloor() - 1).acquire();
    }

    public void releaseAccess(Elevator elevator) {
        service.get(elevator.getFloor() - 1).release();
    }

    public void acquirePickup(Elevator elevator) throws InterruptedException {
        service.get(elevator.getFloor() - 1).acquire();
        pickup.get(elevator.getFloor() - 1).acquire();
    }

    public void releasePickup(Elevator elevator) {

        pickup.get(elevator.getFloor() - 1).release();
        service.get(elevator.getFloor() - 1).release();

    }

}
