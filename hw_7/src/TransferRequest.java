import com.oocourse.elevator3.PersonRequest;

public class TransferRequest extends PersonRequest {

    private int finalToFloor;

    public TransferRequest(PersonRequest personRequest, int finalToFloor) {
        super(personRequest.getFromFloor(),
                personRequest.getToFloor(),
                personRequest.getPersonId());
        this.finalToFloor = finalToFloor;
    }

    public TransferRequest(int fromFloor, int toFloor, int personId, int finalToFloor) {
        super(fromFloor, toFloor, personId);
        this.finalToFloor = finalToFloor;
    }

    public int getFinalToFloor() {
        return finalToFloor;
    }

    public void setFinalToFloor(int finalToFloor) {
        this.finalToFloor = finalToFloor;
    }
}
