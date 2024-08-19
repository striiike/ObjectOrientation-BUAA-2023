import com.oocourse.elevator2.TimableOutput;

public class Magic {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();


        // needs to fix on maintain out
        Supervisor supervisor = new Supervisor();
        RequestQueue queue = new RequestQueue();
        Scheduler scheduler = new Scheduler(queue, supervisor);
        InputThread inputThread = new InputThread(scheduler, queue);
        inputThread.start();

    }

}