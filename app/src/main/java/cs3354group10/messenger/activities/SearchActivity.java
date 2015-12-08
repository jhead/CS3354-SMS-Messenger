package cs3354group10.messenger.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;

/**
 * Activity created to allow the user to search the message database for a desired
 * string or substring of a message.
 */
public class SearchActivity extends ListActivity {

    /** Holds the name of the contact that the user clicks on, and sends it with the intent to
     * {@link cs3354group10.messenger.activities.ThreadViewActivity}
     *
     */
    public final static String THREAD_CONTACT = "Contact name will be stored here by intent";

    /** Stores the substring inputed by the user */
    private EditText inputText;

    /** ListAdapter used to populate the ListActivity*/
    private ListAdapter listAdapter;

    //Used for displaying the items in the view
    private String[] fromColumn = {Message.DB_COLUMN_NAME_CONTACT, Message.DB_COLUMN_NAME_TEXT};
    private int[] toView = {R.id.searchResultContact, R.id.searchResultText};

    private Cursor messageResultCursor;

    /**
     * Sets up the listener for the search button, and adds the functionality for the user to
     * enter search text in the editText field.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputText = (EditText) findViewById(R.id.searchText);

        final Button button = (Button) findViewById(R.id.searchButton);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = inputText.getText().toString();

                searchMessages(str);
            }
        });
    }

    /**
     * Queries the message database for a string/substring,
     * and populates the listview adapter with the results
     * Note: Incomplete version of this function, only returns true right now, no actions for false,
     * but no harm is done if it doesn't find a match. The list will simply not be populated.
     * @param searchStr The string to be queried for
     * @return Returns a boolean based on whether or not the search is found.
     */
    public boolean searchMessages(String searchStr){



        //DEBUG MESSAGES
        //this.deleteDatabase(MessageDatabaseHelper.DATABASE_PATH);
        Context context = getApplicationContext();
        messageResultCursor = MessageDatabase.queryMessagesForString(context, searchStr);

        if (messageResultCursor.getCount() > 0) {
            listAdapter = new SimpleCursorAdapter(this, R.layout.search_list_item, messageResultCursor,
                    fromColumn, toView, 0);

            setListAdapter(listAdapter);
        }
        else{
            setListAdapter(null);
            return false;
        }

        return true;
    }

    /**
     * Whenever a user clicks on one of the search results, this function gets the data
     * from that item, and creates a new intent containing the contact name to switch the activity
     * to the respective {@link cs3354group10.messenger.activities.ThreadViewActivity}
     * @param l The listView inherited from android's ListActivity
     * @param v The activity view
     * @param position The position of the clicked item in the listView
     * @param id The item's unique id
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        SimpleCursorAdapter adapter = (SimpleCursorAdapter) listAdapter;
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);

        //Get the name of the contact that we clicked on
        String contactName = cursor.getString(cursor.getColumnIndex("contact"));
        Log.d("mes", contactName);


        Intent intent = new Intent(this, ThreadViewActivity.class);
        intent.putExtra(THREAD_CONTACT, contactName);
        startActivity(intent);
    }

}
