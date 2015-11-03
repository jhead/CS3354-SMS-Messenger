package cs3354group10.messenger.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageState;
import cs3354group10.messenger.db.MessageDatabase;

import group10.cs3354.sms_messenger.R;


/**
 * EditMessageActivity
 * Probably either replaced entirely by ThreadViewActivity to allow sending and viewing messages at same time,
 * or add similar functionality to ThreadViewActivity.
 * In that case, use this for composing messages not yet part of a thread.
 */
public class EditMessageActivity extends Activity {

    public static EditMessageActivity activityInstance = null;
    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        activityInstance = this;
    }

    @Override
    protected void onResume(){
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        active = false;
        activityInstance = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_message, menu);
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

    /**
     * onSendPressed
     * sends the message
     * @param view Not used
     * TODO: need to add way for multiple contacts, send by contact and not phone number
     */
    public void onSendPressed(View view){
        String message = ((EditText) findViewById(R.id.id_message_field)).getText().toString();
        String address = ((EditText) findViewById(R.id.id_phone_field)).getText().toString();

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(address, null, message, null, null);

        //TODO: check if contact in database and use that contact if it is
        Contact c = new Contact(address);

        //stick in database
        MessageDatabase.insertMessage(getApplicationContext(), new Message(c, message, MessageState.SENT));

        //TODO: probably switch activity elsewhere
        //TODO: probably add confirmation for received messages
        Intent i = new Intent(this,ThreadListActivity.class);
        startActivity(i);
    }
}