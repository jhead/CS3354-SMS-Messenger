package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.Collection;
import java.util.HashMap;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.db.MessageDatabase;
import group10.cs3354.sms_messenger.R;

public class ThreadViewActivity extends ListActivity {

    private SimpleCursorAdapter listAdapter;

    private static final HashMap<String, Integer> listValueMap = new HashMap<>();
    static {
        listValueMap.put(Message.DB_COLUMN_NAME_CONTACT, R.id.threadListItemContactName);
        listValueMap.put(Message.DB_COLUMN_NAME_TEXT, R.id.threadListItemMessage);
    }

    private static CursorAdapter createCursorAdapter(ThreadViewActivity activity, int listItemLayout, Cursor cursor, int flags) {
        Collection<String> keys = listValueMap.keySet();

        Collection<Integer> values = listValueMap.values();
        Integer[] integerValues = (Integer[]) values.toArray(new Integer[values.size()]);

        int[] to = new int[integerValues.length];
        for (int i = 0; i < integerValues.length; i++) {
            to[i] = integerValues[i].intValue();
        }

        String[] from = (String[]) keys.toArray(new String[keys.size()]);

        return new SimpleCursorAdapter(
                activity,
                listItemLayout,
                cursor,
                from,
                to,
                flags
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        Intent intent = getIntent();
        //String contactName = intent.getStringExtra(ThreadListActivity.THREAD_CONTACT);
        //Log.d("name", contactName);
        Contact contact = findContact(intent.getStringExtra(ThreadListActivity.THREAD_CONTACT));

        setTitle(contact.getName()); //Set the title of the activity to the person we are messaging

        Cursor cursor = MessageDatabase.queryMessages(this, contact);

        // TODO fix the cursor (query) to display the messages from a contact

        listAdapter = (SimpleCursorAdapter) createCursorAdapter(this, R.layout.list_row_layout_send, cursor, 0);
        setListAdapter(listAdapter);
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

    private Contact findContact(String name){
        for(Contact contact : Contact.contactList){
            if (contact.getName().equals(name)){
                return contact;
            }
            Log.d("Cont", contact.getName());
        }
        return new Contact("IM BROKEN");
    }
}



