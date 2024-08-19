import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.Request;

import java.io.IOException;

public class InputThread extends Thread {

    private final Scheduler scheduler;
    private final RequestQueue waitQueue;

    public InputThread(Scheduler scheduler, RequestQueue queue) {
        this.scheduler = scheduler;
        this.waitQueue = queue;
        this.scheduler.start();

    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();

            if (request == null) {
                scheduler.setClosed(true);
                break;
            } else {
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
