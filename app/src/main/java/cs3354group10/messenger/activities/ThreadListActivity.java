package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;

public class ThreadListActivity extends ListActivity {

    private static final HashMap<String, Integer> listValueMap = new HashMap<>();
    static {
        listValueMap.put(Message.DB_COLUMN_NAME_CONTACT, R.id.threadListItemContactName);
        listValueMap.put(Message.DB_COLUMN_NAME_TEXT, R.id.threadListItemMessage);
    };

    private SimpleCursorAdapter listAdapter;

    private static CursorAdapter createCursorAdapter(ThreadListActivity activity, int listItemLayout, Cursor cursor, int flags) {
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
        setContentView(R.layout.activity_thread_list);

        this.deleteDatabase(MessageDatabaseHelper.DATABASE_PATH);

        /*** DEBUG: Insert test data into database ***/
        Contact contactJustin = new Contact("Justin Head");
        Contact contactSatsuki = new Contact("Satsuki Ueno");
        Contact contactCristian = new Contact("Cristian Ventura");

        // Display order may differ since the timestamps will probably be identical
        Message messageOne = new Message(contactJustin, "Message #1 from Justin", MessageState.RECV);
        Message messageTwo = new Message(contactSatsuki, "Hello, world!", MessageState.RECV);
        Message messageThree = new Message(contactCristian, "Test 1234", MessageState.RECV);
        Message messageOneTwo = new Message(contactJustin, "Message #2 from Justin", MessageState.RECV);

        MessageDatabase.insertMessage(this, messageOne);
        MessageDatabase.insertMessage(this, messageThree);
        MessageDatabase.insertMessage(this, messageOneTwo);
        MessageDatabase.insertMessage(this, messageTwo);
        /*** DEBUG ***/

        loadThreads();

        final ListView threadList = (ListView) findViewById(R.id.list);

        threadList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem = threadList.getItemAtPosition(position);
            }
        });

    }

    protected void loadThreads() {
        Context context = getApplicationContext();
        Cursor threadListCursor = MessageDatabase.queryThreads(context);

        listAdapter = (SimpleCursorAdapter) createCursorAdapter(this, R.layout.thread_list_item, threadListCursor, 0);
        setListAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thread_list, menu);
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
