package cs3354group10.messenger;

public enum MessageState {

    // A "sent" message is one that the user sent to someone else.
    SENT(0),

    // A "recv" message is one that the user received from someone else.
    RECV(1);

    private final int value;
    MessageState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
