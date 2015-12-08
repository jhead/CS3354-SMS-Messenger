package cs3354group10.messenger.test;

import android.test.ActivityInstrumentationTestCase2;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.activities.EditMessageActivity;


public class EditMessageActivityTest extends ActivityInstrumentationTestCase2<EditMessageActivity> {

    EditMessageActivity e;


    public EditMessageActivityTest(){
        super(EditMessageActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        e = getActivity();
    }

    @Override
    protected void tearDown() throws Exception{
        super.tearDown();
    }

    /**
     * testCreateMessage
     * tests if message is created correctly
     */
    public void testCreateMessage() {
        Message m = new Message(new Contact("1111"),"Hi!",MessageState.SENT);

        Message f = e.createMessage("1111", "Hi!", MessageState.SENT);

        assertEquals(f.getContact().getName(),m.getContact().getName());
        assertEquals(m.getText(),f.getText());
        assertEquals(m.getState(),f.getState());
    }



}