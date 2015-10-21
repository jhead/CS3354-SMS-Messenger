package cs3354group10.messenger.activities;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import group10.cs3354.sms_messenger.R;

public class EditMessageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);
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
     * TODO: need to add way for multiple contacts, send by ontact and not phone number
     */
    public void onSendPressed(View view){
        String message = ((EditText) findViewById(R.id.id_message_field)).getText().toString();
        String address = ((EditText) findViewById(R.id.id_phone_field)).getText().toString();

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(address,null,message,null,null);

        //TODO: probably switch activity or something, save to database, etc
    }
}
