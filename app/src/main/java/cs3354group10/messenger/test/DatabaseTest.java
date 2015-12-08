package cs3354group10.messenger.test;

import android.test.AndroidTestCase;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;

public class DatabaseTest extends AndroidTestCase {

    protected Contact testContact;

    protected void setUp() {
        testContact = new Contact("testContactName");

    }

    protected void tearDown() {
        //
    }

    public void testInsertSentMessage() {
        Message testMessage = new Message(testContact, "Test Message Text", MessageState.SENT);

        long result = MessageDatabase.insertMessage(getContext(), testMessage);

        // SQLite insert error
        assertNotSame("SQLite error occurred", result, -1);
    }

}
