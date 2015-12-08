package cs3354group10.messenger;

/**
 * Represents the state of a message in terms of being sent, received, or a draft.
 */
public enum MessageState {

    // A "sent" message is one that the user sent to someone else.
    SENT(0),

    // A "recv" message is one that the user received from someone else.
    RECV(1),

    // A "draft" message is one that the user created and saved, but has not yet sent
    DRAFT(2);

    private final int value;
    MessageState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageState valueOf(int i) {
        for (MessageState state : MessageState.values()) {
            if (state.getValue() == i) {
                return state;
            }
        }

        return null;
    }

}
