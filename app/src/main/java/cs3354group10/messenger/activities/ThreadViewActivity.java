package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.ThreadViewBinder;
import cs3354group10.messenger.db.MessageDatabase;
import group10.cs3354.sms_messenger.R;


public class ThreadViewActivity extends ListActivity {

    private SimpleCursorAdapter listAdapter;
    private SimpleCursorAdapter.ViewBinder binder;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_TEXT, Message.DB_COLUMN_NAME_STATE};
    private int[] toView = {R.id.threadViewItemMessage};
    private Contact contact;
    private static ThreadViewActivity instance = null;
    private static boolean active = false;

    public static final String FORWARD_MESSAGE = "Message to forward";
    private Cursor threadViewCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        binder = new ThreadViewBinder();

        setContentView(R.layout.activity_thread_view);

        Intent intent = getIntent();

        contact = findContact(intent.getStringExtra(ThreadListActivity.THREAD_CONTACT));
        setTitle(contact.getName());
        loadMessages(contact.getName());
    }

    @Override
    protected void onPause(){
        super.onPause();
        active = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        active = true;
        loadMessages(this.contact.getName());
    }

    public static void updateMessages(){
        if (active)
            instance.loadMessages(instance.contact.getName());
    }


    private Contact findContact(String name){
        for(Contact contact : Contact.contactList){
            if (contact.getName().equals(name)){
                return contact;
            }
            Log.d("Cont", contact.getName());
        }
        return new Contact(name);
    }

    protected void loadMessages(String contact) {
        Context context = getApplicationContext();
        threadViewCursor = MessageDatabase.queryMessages(context, contact);

        listAdapter = new SimpleCursorAdapter(this, R.layout.thread_view_item, threadViewCursor, fromColumn, toView, 0);
        listAdapter.setViewBinder(binder);

        setListAdapter(listAdapter);

        getListView().setSelection(threadViewCursor.getCount() - 1);


            /*
             * To call delete menu by click-and-hold the message.
             * Make sure that onCreateContextMenu() and onContextItemSelected are called.
             */
            registerForContextMenu(getListView());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Message Options");

        String[] menuItems;

        int messageIndex = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        threadViewCursor.moveToPosition(messageIndex);

        Message message = Message.fromCursor(threadViewCursor);

        Log.d("debug", message.getText());
        Log.d("debug", message.getState().toString());

        if (message.getState().equals(MessageState.DRAFT)) {
            menuItems = new String[] { "Delete", "Forward", "Edit Draft" };
        } else {
            menuItems = new String[] { "Delete", "Forward" };
        }

        // Add items to menu
        for (int i = 0; i < menuItems.length; i++)
            menu.add(menuItems[i]);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItem = (String) item.getTitle();

        Context context = getApplicationContext();
        // Get the extra information set by ListView aka the message
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        // row id of the item for which the context menu is being displayed.
        int messageID = info.position;
        // Point to the message, get the content and delete it
        threadViewCursor.moveToPosition(messageID);

        Message message = Message.fromCursor(threadViewCursor);

        Intent intent;
        String origContact = message.getContact().getName();
        MessageState state = message.getState();

        switch (menuItem) {
            case "Delete":
                // TODO: Confirm deletion

                String timeStamp = threadViewCursor.getString(threadViewCursor.getColumnIndex(Message.DB_COLUMN_NAME_TIMESTAMP));
                MessageDatabase.deleteMessage(this, message.getText(), message.getTimestamp());

                // Reload the view and ThreadList
                loadMessages(this.contact.getName());
                break;

            case "Forward":
                intent = new Intent(this, EditMessageActivity.class);
                intent.putExtra(EditMessageActivity.EXTRA_MESSAGE, message.getText());

                if (state.equals(MessageState.RECV))
                    intent.putExtra(FORWARD_MESSAGE, "Fowarded message from " + origContact + ": " + message);
                else
                    intent.putExtra(FORWARD_MESSAGE, "Fowarded message originally sent to " + origContact +": " + message);

                startActivity(intent);
                break;

            case "Edit Draft":
                intent = new Intent(this, EditMessageActivity.class);

                intent.putExtra(EditMessageActivity.EXTRA_RECIPIENTS, message.getContact().getName());
                intent.putExtra(EditMessageActivity.EXTRA_MESSAGE, message.getText());

                MessageDatabase.deleteDraft(this, message);

                startActivity(intent);
                break;
            default:
                // Do nothing
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thread_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSendPressed(View view){
        String message = ((EditText) findViewById(R.id.threadView_messageEditor)).getText().toString();

        String address = getNumber(contact.getName());

        if (address == null) {
            Toast t = Toast.makeText(this, "Error finding phone number for contact.", Toast.LENGTH_SHORT);
            t.show();
            return;
        }

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(address, null, message, null, null);

        String recipient = contactExists(contact.getName());

        //stick in database
        MessageDatabase.insertMessage(getApplicationContext(), new Message(contact, message, MessageState.SENT));
        loadMessages(contact.getName());

        EditText e = (EditText) findViewById(R.id.threadView_messageEditor);
        e.setText("", TextView.BufferType.EDITABLE);
    }


    private String contactExists( String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                String FirstName =cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                //String LastName =cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.))
                return FirstName;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return number;
    }

    private String getNumber(String name){
        Cursor cur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?", new String[] { name }, null);
        try {
            if (cur.moveToFirst()) {
                String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                return number;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return name; //when name is number (when contact doesnt exist)
    }

}
