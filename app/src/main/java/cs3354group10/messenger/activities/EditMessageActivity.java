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

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String EXTRA_RECIPIENTS = "EXTRA_RECIPIENTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_message);

        activityInstance = this;

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_MESSAGE)) {
            setMessageText(intent.getStringExtra(EXTRA_MESSAGE));
        }

        if (intent.hasExtra(EXTRA_RECIPIENTS)) {
            setMessageRecipients(Contact.resolveNumber(this, intent.getStringExtra(EXTRA_RECIPIENTS)));
        }
    }

    public void onClickAddTo(View V){
        insertNewContact();
    }
    public void insertNewContact()
    {
        String mobileNumber =(((EditText) findViewById(R.id.id_phone_field)).getText().toString());
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, mobileNumber);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        startActivity(intent);
    }

    public void onCancelPressed(View v){
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
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_save:
                return onActionSave();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean onActionSave() {
        String messageText = getMessageText();
        String recipients = getMessageRecipients();

        Message draft = createMessage(recipients, messageText, MessageState.DRAFT);
        MessageDatabase.insertMessage(this, draft);

        Intent intent = new Intent(this, ThreadListActivity.class);
        startActivity(intent);

        return true;
    }

    private Message createMessage(String recipients, String messageText, MessageState state) {
        if (messageText == null || messageText.length() == 0) {
            Toast.makeText(this,"Message is empty!",Toast.LENGTH_SHORT).show();
            return null;
        }

        if (recipients == null || recipients.length() == 0){
            Toast.makeText(this,"Recipient not selected!",Toast.LENGTH_SHORT).show();
            return null;
        }

        String[] addresses = recipients.split(";");
        recipients = "";

        for (String address : addresses) {
            recipients += Contact.resolveName(this, address) + ",";
        }

        if (recipients.length() > 0) {
            recipients = recipients.substring(0, recipients.length() - 1);
        }

        Contact contact = new Contact(recipients);

        return new Message(contact, messageText, state);
    }

    private EditText getMessageTextField() {
        return (EditText) findViewById(R.id.id_message_field);
    }

    private void setMessageText(String text) {
        getMessageTextField().setText(text, TextView.BufferType.EDITABLE);
    }

    public String getMessageText() {
        return getMessageTextField()
                .getText()
                .toString()
                .trim();
    }

    private EditText getMessageRecipientsField() {
        return (EditText) findViewById(R.id.id_phone_field);
    }

    private void setMessageRecipients(String recipients) {
        getMessageRecipientsField().setText(recipients, TextView.BufferType.EDITABLE);
    }

    public String getMessageRecipients() {
        return getMessageRecipientsField()
                .getText()
                .toString()
                .trim();
    }

    /**
     * onSendPressed
     * sends the message, multiple contacts indicated by splitting with ';'
     * @param view Not used
     */
    public void onSendPressed(View view){
        SmsManager manager = SmsManager.getDefault();

        String messageText = getMessageText();
        Message message = createMessage(getMessageRecipients(), messageText, MessageState.SENT);

        if (message == null) {
            // Message failed to send
            return;
        }

        String[] recipients = getMessageRecipients().split(";");
        for (String a : recipients) {
            try {
                manager.sendTextMessage(a, null, messageText, null, null);
            } catch (Exception ex) {
                // Message failed to send
            }
        }

        //stick in database
        MessageDatabase.insertMessage(this, message);

        //switch activity
        Intent i = new Intent(this, ThreadListActivity.class);
        startActivity(i);
    }

}