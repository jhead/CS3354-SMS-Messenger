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

public class SearchActivity extends ListActivity {

    public final static String THREAD_CONTACT = "Contact name will be stored here by intent";
    private EditText inputText;
    private ListAdapter listAdapter;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_CONTACT, Message.DB_COLUMN_NAME_TEXT};
    private int[] toView = {R.id.searchResultContact, R.id.searchResultText};
    private Cursor messageResultCursor;


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

    private boolean searchMessages(String searchStr){



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
        }

        return true;
    }

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
