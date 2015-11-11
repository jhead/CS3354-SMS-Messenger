package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.Collection;
import java.util.HashMap;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;


public class ThreadListActivity extends ListActivity {


    public final static String THREAD_CONTACT = "Contact name will be stored here by intent";
    // Key to query the extra data
//    public final static String CONTACT = "cs3354group10.messenger.activities.CONTACT";
    private static boolean active = false;
    private static ThreadListActivity activityInstance;

    private static final HashMap<String, Integer> listValueMap = new HashMap<>();

    static {
        listValueMap.put(Message.DB_COLUMN_NAME_CONTACT, R.id.threadListItemContactName);
        listValueMap.put(Message.DB_COLUMN_NAME_TEXT, R.id.threadListItemMessage);
    }

    ;

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

        activityInstance = this;
        
        loadThreads();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        active = false;
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

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    /**
     * updateThreads
     * called by SMSBroadcastReceiver on receiving a message to refresh display
     */
    public static void updateThreads() {
        if (active)
            activityInstance.loadThreads();
    }

    public void onCreateClick(View v){
        startActivity(new Intent( cs3354group10.messenger.activities.ThreadListActivity.this , cs3354group10.messenger.activities.EditMessageActivity.class ));
    }
    public void onSearchClick(View v){
        startActivity(new Intent( cs3354group10.messenger.activities.ThreadListActivity.this , cs3354group10.messenger.activities.SearchActivity.class ));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = listAdapter.getCursor();
        cursor.moveToPosition(position);

        //Get the name of the contact that we clicked on
        String contactName = cursor.getString(cursor.getColumnIndex("contact"));
        Log.d("mes", contactName);


        Intent intent = new Intent(this, ThreadViewActivity.class);
        intent.putExtra(THREAD_CONTACT, contactName);
        startActivity(intent);
    }


    public void onClick(View view){
        Intent i = new Intent(this, EditMessageActivity.class);
        startActivity(i);
    }
}





