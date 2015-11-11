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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.ThreadViewBinder;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;

public class ThreadViewActivity extends ListActivity {

    private SimpleCursorAdapter listAdapter;
    private SimpleCursorAdapter.ViewBinder binder;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_TEXT, Message.DB_COLUMN_NAME_STATE};
    private int[] toView = {R.id.threadViewItemMessage};
    private Contact contact;
    private static ThreadViewActivity instance = null;
    private static boolean active = false;

    //states used to determine what to do when a message is clicked
    private static final int STATE_NORMAL = 0;
    private static final int STATE_FORWARD = 1;
    private int state = 0;

    public static final String FORWARD_MESSAGE = "Message to forward";


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
        loadMessages(contact.getName());
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
        Cursor threadViewCursor = MessageDatabase.queryMessages(context, contact);

        listAdapter = new SimpleCursorAdapter(this, R.layout.thread_view_item, threadViewCursor, fromColumn, toView, 0);
        listAdapter.setViewBinder(binder);

        setListAdapter(listAdapter);

        getListView().setSelection(threadViewCursor.getCount() - 1);
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
        if (id == R.id.view_forward) {
            state = STATE_FORWARD;
            setTitle("Forward");
            Button b = (Button)findViewById(R.id.button);
            b.setText("Cancel");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSendPressed(View view){
        switch (state) {
            case STATE_NORMAL:
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
                break;

            case STATE_FORWARD:
                state = STATE_NORMAL;
                Button b = (Button) findViewById(R.id.button);
                b.setText("Send");
                setTitle(contact.getName());
                break;
        }
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


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position,id);
        switch(state){
            case STATE_NORMAL:
                //do nothing
                break;

            case STATE_FORWARD:
                state = STATE_NORMAL;
                //copy data and move to new activity
                Cursor cursor = listAdapter.getCursor();
                cursor.moveToPosition(position);
                String message = cursor.getString(cursor.getColumnIndex(Message.DB_COLUMN_NAME_TEXT));

                Intent intent = new Intent(this, EditMessageActivity.class);
                intent.putExtra(FORWARD_MESSAGE, "Fwd: " + message);
                startActivity(intent);
                break;
        }
    }
}
