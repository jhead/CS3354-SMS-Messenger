package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;

public class ThreadViewActivity extends ListActivity {

    private ListAdapter listAdapter;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_TEXT};
    private int[] toView = {R.id.threadViewItemMessage};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_view);

        Intent intent = getIntent();
        Contact contact = findContact(intent.getStringExtra(ThreadListActivity.THREAD_CONTACT));
        setTitle(contact.getName());
        loadMessages(contact.getName());
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

    protected void loadMessages(String contact) {
        Context context = getApplicationContext();
        Cursor threadViewCursor = MessageDatabase.queryMessages(context, contact);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.thread_view_item,
                threadViewCursor,
                fromColumn,
                toView,
                0
        );

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
}
