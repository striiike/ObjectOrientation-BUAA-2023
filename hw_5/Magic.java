import com.oocourse.elevator2.TimableOutput;

public class Magic {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();

        RequestQueue queue = new RequestQueue();
        Scheduler scheduler = new Scheduler(queue);
        InputThread inputThread = new InputThread(scheduler, queue);
        inputThread.start();

    }

}