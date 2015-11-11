package cs3354group10.messenger.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import group10.cs3354.sms_messenger.R;

public class SearchActivity extends ListActivity {

    EditText inputText;

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
        

        return true;
    }



}
