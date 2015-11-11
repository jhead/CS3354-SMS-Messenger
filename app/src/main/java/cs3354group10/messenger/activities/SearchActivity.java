package cs3354group10.messenger.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;
import cs3354group10.messenger.db.MessageDatabaseHelper;
import group10.cs3354.sms_messenger.R;

public class SearchActivity extends ListActivity {

    EditText inputText;
    private ListAdapter listAdapter;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_TEXT};
    private int[] toView = {R.id.searchResult};
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
        this.deleteDatabase(MessageDatabaseHelper.DATABASE_PATH);
        Contact brendan = new Contact("Brendan");
        Contact satsuki = new Contact("Satsuki");
        Message message1 = new Message(brendan, "test", MessageState.RECV);
        Message message2 = new Message(brendan, "another test", MessageState.RECV);
        Context context = getApplicationContext();
        MessageDatabase.insertMessage(context, message1);
        MessageDatabase.insertMessage(context, message2);
        messageResultCursor = MessageDatabase.queryMessagesForString(context, searchStr);

        listAdapter = new SimpleCursorAdapter(this, R.layout.search_list_item, messageResultCursor,
                fromColumn, toView, 0);

        setListAdapter(listAdapter);


        return true;
    }



}
