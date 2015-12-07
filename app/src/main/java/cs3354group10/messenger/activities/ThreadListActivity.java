package cs3354group10.messenger.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Collection;
import java.util.HashMap;

import cs3354group10.messenger.Message;
import cs3354group10.messenger.db.MessageDatabase;


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

    /**
     * Creates the CursorAdapter necessary to map message threads from the database to this ListActivity.
     *
     * @param activity Current activity (ListActivity)
     * @param listItemLayout List layout ID
     * @param cursor Database query result cursor
     * @param flags SimpleCursorAdapter flags
     * @return
     */
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

    /**
     * Activity create event. Initializes activity and loads threads.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);

        activityInstance = this;
        
        loadThreads();
    }

    /**
     * Set activity inactive and remove reference to self.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
        active = false;
    }

    /**
     * Loads message threads from database into adapter and updates the list displayed on-screen.
     */
    protected void loadThreads() {
        Context context = getApplicationContext();
        Cursor threadListCursor = MessageDatabase.queryThreads(context);

        listAdapter = (SimpleCursorAdapter) createCursorAdapter(this, R.layout.thread_list_item, threadListCursor, 0);
        setListAdapter(listAdapter);
        registerForContextMenu(getListView());
    }

    /**
     * Creates the options menu for this activity.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thread_list, menu);
        return true;
    }

    /**
     * Handles options menu item selection event.
     *
     * Handled internally by Android.
     *
     * @param item Selected item
     * @return
     */
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

    /**
     * Marks the activity as active.
     */
    @Override
    protected void onResume() {
        super.onResume();
        active = true;
        updateThreads();
    }


    /**
     * Marks the activity as inactive.
     */
    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }

    /**
     * Called by SMSBroadcastReceiver on receiving a message to refresh display.
     */
    public static void updateThreads() {
        if (active)
            activityInstance.loadThreads();
    }

    /**
     * Called when "Create Message" button is clicked.
     *
     * @param v
     */
    public void onCreateClick(View v){
        startActivity(new Intent( cs3354group10.messenger.activities.ThreadListActivity.this , cs3354group10.messenger.activities.EditMessageActivity.class ));
    }

    /**
     * Called when "Search" button is clicked.
     *
     * @param v
     */
    public void onSearchClick(View v){
        startActivity(new Intent( cs3354group10.messenger.activities.ThreadListActivity.this , cs3354group10.messenger.activities.SearchActivity.class ));
    }

    /**
     * Called when a thread in the list is clicked.
     *
     * @param l Current activity (ListView/ThreadListActivity)
     * @param v
     * @param position Position in list of clicked item
     * @param id
     */
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

    /**
     * Initializes context (hold) menu, primarily for deleting message threads.
     *
     * @param menu ContextMenu instance
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Options");
        String[] menuItems = {"Delete"};
        // Add items to menu
        for (int i = 0; i < menuItems.length; i++)
            menu.add(menuItems[i]);
    }

    /**
     * Called when the "Delete" context menu button is selected.
     *
     * The only button available is the "Delete" button.
     *
     * @param item Selected menu item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String menuItem = (String) item.getTitle();
        switch (menuItem) {
            case "Delete":
                // TODO: Confirm deletion
                Context context = getApplicationContext();
                // Get the extra information set by ListView aka the thread
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                // row id of the item for which the context menu is being displayed.
                int threadID = info.position;
                // Point to the thread, get the content and delete it
                Cursor threadListCursor = listAdapter.getCursor();
                threadListCursor.moveToPosition(threadID);

                String contact = threadListCursor.getString(threadListCursor.getColumnIndex(Message.DB_COLUMN_NAME_CONTACT));
                MessageDatabase.deleteThread(context, contact);

                // Reload the view
                updateThreads();
                break;
        }
        return true;
    }
}





