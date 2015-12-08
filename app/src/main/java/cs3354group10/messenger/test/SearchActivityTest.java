package cs3354group10.messenger.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.activities.SearchActivity;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;

public class SearchActivityTest extends ActivityInstrumentationTestCase2<SearchActivity> {

    protected Contact testContact;
    protected SearchActivity testActivity;

    public SearchActivityTest() {
        super(SearchActivity.class);
    }

    @Override
    protected void setUp() throws Exception{

        super.setUp();
        testActivity = getActivity();
        testContact = new Contact("testContactName");
        Message testMessage1 = new Message(testContact, "Test Message Text", MessageState.SENT);
        MessageDatabase.insertMessage(testActivity.getApplicationContext(), testMessage1);
        Message testMessage2 = new Message(testContact, "Hello", MessageState.RECV);
        MessageDatabase.insertMessage(testActivity.getApplicationContext(), testMessage2);
        Message testMessage3 = new Message(testContact, "Another Test", MessageState.SENT);
        MessageDatabase.insertMessage(testActivity.getApplicationContext(), testMessage3);

    }

    protected void tearDown() {
        getContext().deleteDatabase(MessageDatabaseHelper.DATABASE_PATH);
    }

    public void testMessageSearch() {

        assertTrue("The substring 'Tes' exists in some message",
                testActivity.searchMessages("tes"));

        assertTrue("The substring 'Hello' exists in some message",
                testActivity.searchMessages("Hello"));

        assertTrue("The substring 'other' exists in some message",
                testActivity.searchMessages("other"));

        assertTrue("The substring 'Brendan' exists in some message (should fail)",
                testActivity.searchMessages("Brendan"));
    }

}