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
import group10.cs3354.sms_messenger.R;

public class SearchActivity extends ListActivity {

    EditText inputText;
    private ListAdapter listAdapter;
    private String[] fromColumn = {Message.DB_COLUMN_NAME_TEXT};
    private int[] toView = {R.id.searchResult};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputText = (EditText) findViewById(R.id.searchText);

        final Button button = (Button) findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String str = inputText.getText().toString();

                if(searchMessages(str)){

                }
                else{

                }

            }
        });
    }

    private boolean searchMessages(String searchStr){
        //DEBUG MESSAGES



        Context context = getApplicationContext();
        MessageDatabase.insertMessage(context, new Message(new Contact("Brendan"), "test", MessageState.RECV));
        Cursor messageResultCursor = MessageDatabase.queryMessagesForString(context, searchStr);

        listAdapter = new SimpleCursorAdapter(this, R.layout.search_list_item, messageResultCursor,
                fromColumn, toView, 0);

        setListAdapter(listAdapter);
        return true;
    }



}
