package cs3354group10.messenger.test;

import android.content.Context;
import android.test.AndroidTestCase;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;

public class DatabaseTest extends AndroidTestCase {

    protected final Contact testContact = new Contact("testContactName");

    public static void deleteDatabase(Context context) {
        MessageDatabaseHelper.getInstance(context).close();
        context.deleteDatabase(MessageDatabaseHelper.DATABASE_PATH);
    }

    protected void setUp() {
        deleteDatabase(getContext());
    }

    protected void tearDown() {
        deleteDatabase(getContext());
    }

    public void testInsertMessage() {
        Message testMessage = new Message(testContact, "Test Message Text", MessageState.SENT);

        long result = MessageDatabase.insertMessage(getContext(), testMessage);
        assertFalse("SQLite error occurred", result == -1);
    }

    public void testDeleteDraft() {
        Message testMessage = new Message(testContact, "Test Draft Text", MessageState.DRAFT);

        // Insert a draft so that we can delete it
        MessageDatabase.insertMessage(getContext(), testMessage);

        long result = MessageDatabase.deleteDraft(getContext(), testMessage);
        assertEquals("Failed to delete draft, wrong number of rows were removed", result, 1);
    }

}
