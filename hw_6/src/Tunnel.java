import com.oocourse.elevator2.PersonRequest;

import java.util.ArrayList;

public class Tunnel {

    private ArrayList<PersonRequest> requestOutside;
    private boolean isMaintaining;
    private boolean isClosed;
    private boolean isMaintained;
    private boolean isClosing;

    public Tunnel() {
        isMaintained = false;
        isClosed = false;
        isMaintaining = false;
        isClosing = false;
        requestOutside = new ArrayList<>();
    }

    public synchronized boolean isMaintaining() {
        return isMaintaining;
    }

    public synchronized void setMaintaining(boolean maintaining) {
        isMaintaining = maintaining;
        notifyAll();
    }

    public synchronized  boolean isClosed() {
        return isClosed;
    }

    public synchronized void setClosed(boolean closed) {
        isClosed = closed;
        notifyAll();
    }

    public synchronized boolean isMaintained() {
        return isMaintained;
    }

    public synchronized void setMaintained(boolean maintained) {
        isMaintained = maintained;
        notifyAll();
    }

    public boolean isClosing() {
        return isClosing;
    }

    public void setClosing(boolean closing) {
        isClosing = closing;
    }

    public ArrayList<PersonRequest> getRequestOutside() {
        return requestOutside;
    }

    public void setRequestOutside(ArrayList<PersonRequest> requestOutside) {
        this.requestOutside = requestOutside;
    }
}
