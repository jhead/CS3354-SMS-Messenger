package cs3354group10.messenger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.provider.ContactsContract.Contacts;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

    String DEBUG_TAG = "CS3354-SMS-Messenger";

    public static EditMessageActivity activityInstance = null;
    private static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        activityInstance = this;

        Intent intent = getIntent();
        if (intent.hasExtra(ThreadViewActivity.FORWARD_MESSAGE)){
            EditText e = (EditText)findViewById(R.id.id_message_field);
            e.setText(intent.getStringExtra(ThreadViewActivity.FORWARD_MESSAGE), TextView.BufferType.EDITABLE);
        }
    }



    public void onClickCancel(View v){
        startActivity(new Intent(cs3354group10.messenger.activities.EditMessageActivity.this, cs3354group10.messenger.activities.ThreadListActivity.class));
    }

    private static final int CONTACT_PICKER_RESULT = 1001;

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    final EditText phoneInput = (EditText) findViewById(R.id.id_phone_field);
                    Cursor cursor = null;
                    String phoneNumber = "";
                    List<String> allNumbers = new ArrayList<String>();
                    int phoneIdx = 0;
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id }, null);
                        phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        if (cursor.moveToFirst()) {
                            while (cursor.isAfterLast() == false) {
                                phoneNumber = cursor.getString(phoneIdx);
                                allNumbers.add(phoneNumber);
                                cursor.moveToNext();
                            }
                        } else {
                            //no results actions
                        }
                    } catch (Exception e) {
                        //error actions
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditMessageActivity.this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String selectedNumber = items[item].toString();
                                selectedNumber = selectedNumber.replace("-", "");
                                selectedNumber = selectedNumber.replace("(", "");
                                selectedNumber = selectedNumber.replace(")", "");
                                selectedNumber = selectedNumber.replace(" ", "");
                                phoneInput.setText(selectedNumber);
                            }
                        });
                        AlertDialog alert = builder.create();
                        if(allNumbers.size() > 1) {
                            alert.show();
                        } else {
                            String selectedNumber = phoneNumber.toString();
                            selectedNumber = selectedNumber.replace("-", "");
                            selectedNumber = selectedNumber.replace("(", "");
                            selectedNumber = selectedNumber.replace(")", "");
                            selectedNumber = selectedNumber.replace(" ", "");
                            phoneInput.setText(selectedNumber);
                        }

                        if (phoneNumber.length() == 0) {
                            //no numbers found actions
                        }
                    }
                    break;
            }
        } else {
            //activity result error actions
        }
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


    public String contactExists( String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                String FirstName =cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                //String LastName =cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.))
                return FirstName;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return number;
    }


    /**
     * onSendPressed
     * sends the message, multiple contacts indicated by splitting with ';'
     * @param view Not used
     */
    public void onSendPressed(View view){
        String message = ((EditText) findViewById(R.id.id_message_field)).getText().toString();
        String address [] = ((EditText) findViewById(R.id.id_phone_field)).getText().toString().split(";");

        SmsManager manager = SmsManager.getDefault();
        for (String a : address)
            manager.sendTextMessage(a, null, message, null, null);

        address[0] = contactExists(address[0]);
        Contact c = new Contact(address[0]);

        //stick in database
        MessageDatabase.insertMessage(getApplicationContext(), new Message(c, message, MessageState.SENT));

        //switch activity
        Intent i = new Intent(this,ThreadListActivity.class);
        startActivity(i);
    }
}