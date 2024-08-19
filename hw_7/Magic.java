import com.oocourse.elevator3.TimableOutput;

public class Magic {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();


        // needs to fix on maintain out
        Supervisor supervisor = new Supervisor();
        RequestQueue queue = new RequestQueue();
        Tunnel tunnel = new Tunnel();
        Scheduler scheduler = new Scheduler(queue, supervisor, tunnel);
        InputThread inputThread = new InputThread(scheduler, queue, tunnel);
        inputThread.start();

    }

}