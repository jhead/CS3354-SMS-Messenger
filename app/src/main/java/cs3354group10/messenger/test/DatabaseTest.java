package cs3354group10.messenger.test;

import android.content.Context;
import android.database.Cursor;
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

    public void testDeleteMessage() {
        Message testMessage = new Message(testContact, "Test Message Text", MessageState.SENT);

        // Insert a draft so that we can delete it
        MessageDatabase.insertMessage(getContext(), testMessage);

        // Delete message
        long result = MessageDatabase.deleteMessage(getContext(), testMessage.getText(), testMessage.getTimestamp());

        // Check number of messages deleted, should be 1
        assertEquals("Failed to delete draft, wrong number of rows were removed", result, 1);
    }

    public void testDeleteDraft() {
        Message testMessage = new Message(testContact, "Test Message Text", MessageState.DRAFT);

        // Insert a draft so that we can delete it
        MessageDatabase.insertMessage(getContext(), testMessage);

        // Delete draft
        long result = MessageDatabase.deleteDraft(getContext(), testMessage);

        // Check number of messages deleted, should be 1
        assertEquals("Failed to delete draft, wrong number of rows were removed", result, 1);
    }

    public void testDeleteThread() {
        Message testMessageOne = new Message(testContact, "Test Message Text 1", MessageState.SENT);
        Message testMessageTwo = new Message(testContact, "Test Message Text 2", MessageState.SENT);

        // Insert messages
        MessageDatabase.insertMessage(getContext(), testMessageOne);
        MessageDatabase.insertMessage(getContext(), testMessageTwo);

        // Delete thread (both messages)
        long result = MessageDatabase.deleteThread(getContext(), testContact.getName());

        // Check number of messages deleted, should be 2
        assertEquals("Failed to delete thread, wrong number of rows were removed", result, 2);
    }

    public void testQueryMessageForString() {
        String queryString = "unique-string-12345";
        Message testMessage = new Message(testContact, queryString, MessageState.SENT);

        // Insert message
        MessageDatabase.insertMessage(getContext(), testMessage);

        // Query message
        Cursor cursor = MessageDatabase.queryMessagesForString(getContext(), queryString);

        // Check number of messages returned, should be 1
        assertEquals("Query returned wrong number of messages", cursor.getCount(), 1);
    }

    public void testQueryMessages() {
        Cursor cursor;

        Contact contactOne = testContact;
        Contact contactTwo = new Contact("contact2");

        Message testMessageOne = new Message(contactOne, "Test Message Text 1-1", MessageState.SENT);
        Message testMessageTwo = new Message(contactOne, "Test Message Text 1-2", MessageState.SENT);
        Message testMessageThree = new Message(contactTwo, "Test Message Text 2", MessageState.SENT);

        // Insert two messages from the same contact
        MessageDatabase.insertMessage(getContext(), testMessageOne);
        MessageDatabase.insertMessage(getContext(), testMessageTwo);

        cursor = MessageDatabase.queryMessages(getContext(), contactOne.getName());
        assertEquals("Query returned wrong number of messages", cursor.getCount(), 2);

        // Insert another message from a different contact
        MessageDatabase.insertMessage(getContext(), testMessageThree);

        cursor = MessageDatabase.queryMessages(getContext(), contactOne.getName());
        assertEquals("Query returned wrong number of messages", cursor.getCount(), 2);

        cursor = MessageDatabase.queryMessages(getContext(), contactTwo.getName());
        assertEquals("Query returned wrong number of messages", cursor.getCount(), 1);
    }

    public void testQueryThreads() {
        Cursor cursor;

        Contact contactOne = testContact;
        Contact contactTwo = new Contact("contact2");

        Message testMessageOne = new Message(contactOne, "Test Message Text 1", MessageState.SENT);
        Message testMessageTwo = new Message(contactTwo, "Test Message Text 2", MessageState.SENT);

        // Try with one message
        MessageDatabase.insertMessage(getContext(), testMessageOne);

        cursor = MessageDatabase.queryThreads(getContext());

        assertEquals("Query returned wrong number of threads", cursor.getCount(), 1);

        // Insert a second and try again
        MessageDatabase.insertMessage(getContext(), testMessageTwo);

        cursor = MessageDatabase.queryThreads(getContext());

        assertEquals("Query returned wrong number of threads", cursor.getCount(), 2);
    }



}
