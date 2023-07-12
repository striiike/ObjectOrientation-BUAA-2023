import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.MaintainRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;

public class InputThread extends Thread {

    private final Scheduler scheduler;
    private final RequestQueue waitQueue;
    private Tunnel visor;

    public InputThread(Scheduler scheduler, RequestQueue queue, Tunnel visor) {
        this.visor = visor;
        this.scheduler = scheduler;
        this.waitQueue = queue;
        this.scheduler.start();

    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request instanceof MaintainRequest) {
                for (Elevator elevator : scheduler.getElevators()) {
                    if (elevator.getElevatorId()
                            == ((MaintainRequest) request).getElevatorId()) {
                        // System.out.println("accepted maintaining " + elevator.getElevatorId());
                    }
                }
            }


            if (request == null) {
                scheduler.setClosing(true);
                break;
            } else {
                if (request instanceof MaintainRequest) {
                    synchronized (visor) {
                        for (Elevator elevator : scheduler.getElevators()) {
                            if (elevator.getElevatorId()
                                    == ((MaintainRequest) request).getElevatorId()) {
                                (elevator.getTunnel()).setMaintaining(true);
                            }
                        }
                    }
                }
                waitQueue.addRequest(request);
            }
        }
        try {
            elevatorInput.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
