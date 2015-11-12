package cs3354group10.messenger;

import android.content.ContentValues;
import android.database.Cursor;

public class Message {

    public static final String DB_TABLE_NAME = "messages";
    public static final String DB_COLUMN_NAME_ID = "_id";
    public static final String DB_COLUMN_NAME_CONTACT = "contact";
    public static final String DB_COLUMN_NAME_TIMESTAMP = "timestamp";
    public static final String DB_COLUMN_NAME_TEXT = "text";
    public static final String DB_COLUMN_NAME_STATE = "state";

    public static final String[] DB_COLUMNS = {
            DB_COLUMN_NAME_ID,
            DB_COLUMN_NAME_CONTACT,
            DB_COLUMN_NAME_TIMESTAMP,
            DB_COLUMN_NAME_TEXT,
            DB_COLUMN_NAME_STATE
    };

    // Unique message ID, automatically generated when inserted into the database
    protected int id;

    // The contact of the remote participant
    // Note that this is not the local phone user, even if the message was sent by the user.
    protected Contact contact;

    // Timestamp that the message was sent/received
    protected long timestamp;

    // Message text
    protected String text;

    // Whether the message was sent or received from the user's perspective
    protected MessageState state;

    public Message(int id, Contact contact, long timestamp, String text, MessageState state) {
        this.id = id;
        this.contact = contact;
        this.timestamp = timestamp;
        this.text = text;
        this.state = state;
    }

    public Message(Contact contact, String text, MessageState state) {
        this(-1, contact, System.currentTimeMillis() / 1000, text, state);
    }

    /**
     * Generates a ContentValues object for use when inserting message data into the database
     *
     * @return ContentValues necessary for insert
     */
    public ContentValues getFields() {
        ContentValues values = new ContentValues();

        // Skip ID because it's not used during an insert
        values.put(DB_COLUMN_NAME_CONTACT, getContact().getName());
        values.put(DB_COLUMN_NAME_TIMESTAMP, getTimestamp());
        values.put(DB_COLUMN_NAME_TEXT, getText());
        values.put(DB_COLUMN_NAME_STATE, getState().getValue());

        return values;
    }

    public Contact getContact() {
        return this.contact;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public MessageState getState() {
        return this.state;
    }

    public boolean isDraft() {
        return getState().equals(MessageState.DRAFT);
    }

    public static Message fromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DB_COLUMN_NAME_ID));

        String contactText = cursor.getString(cursor.getColumnIndex(DB_COLUMN_NAME_CONTACT));
        Contact contact = new Contact(contactText);

        String text = cursor.getString(cursor.getColumnIndex(DB_COLUMN_NAME_TEXT));

        MessageState state = MessageState.valueOf(cursor.getInt(cursor.getColumnIndex(DB_COLUMN_NAME_STATE)));

        long timestamp = cursor.getLong(cursor.getColumnIndex(DB_COLUMN_NAME_TIMESTAMP));

        return new Message(id, contact, timestamp, text, state);
    }

}
