package cs3354group10.messenger.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
 * Basic message editing activity with sending capability.
 * Allows editing recipient, choosing recipient by contact, and editing messages.
 */
public class EditMessageActivity extends Activity {

    String DEBUG_TAG = "CS3354-SMS-Messenger";

    /**
     * Reference to instance of activity.
     * Used to call member functions from other classes
     */
    public static EditMessageActivity activityInstance = null;
    /**
     * If activity is currently visible
     */
    private static boolean active = false;

    /**
     * String for describing Extras when forwarding messages.
     * Associated Extra should contain message content.
     */
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    /**
     * String for describing Extras when forwarding messages.
     * Associated Extra should contain recipient phone numbers.
     */
    public static final String EXTRA_RECIPIENTS = "EXTRA_RECIPIENTS";

    /**
     * Overriden create function
     * Accepts Bundle which may contain extras for forwarded messsages.
     * When forwarding a message, Bundle should contain EXTRA_MESSAGE and EXTRA_RECIPIENTS
     * @param savedInstanceState
     */
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

    /**
     * This method calls the insertNewContact() function , which allows the user to add a contact to
     * the stock contact app.
     *
     */
    public void onClickAddTo(View V){
        insertNewContact();
    }

    /**
     * This method takes the number if one has been inputted into id_phone_field and opens
     * the add contact window of the built in stock app, with the phone number already in phone number input as a mobile
     * number, which enables the user to be able to add a new number to the stock contact app
     */
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

    /**
     * This method allows the user to cancel, which takes them back to the threadListActivity, when creating a new message.
     *
     */
    public void onCancelPressed(View v){
        startActivity(new Intent(cs3354group10.messenger.activities.EditMessageActivity.this, cs3354group10.messenger.activities.ThreadListActivity.class));
    }


    private static final int CONTACT_PICKER_RESULT = 1001;

    /**
     * This method opens the view of the contact list of the stock contact app,
     * which then allows you to choose the contact you are trying to message.
     * Then gets the results of what the user chose and calls the function onActivityResult,
     * which handles the results and gets the number of the contact selected.*
     */
    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    /**
     * This method takes the results form the contact chosen, checks if there the actual results , and error did not occur when getting the results.
     * Then checks what type of action was used to get these results.
     * If it was CONTACT_PICKER_ACTION, it gets the phone number associated with that contact name from data,
     * then removes all characters that are not numbers in the contact phone number, and puts it in id_phone_field.
     *
     * @param requestCode Is a code used to know what type of action is being done.
     * @param resultCode Is a code used to know if the results from the Intent were correct results..
     * @param data Is the data from the contact that was chosen.
     */
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


    /**
     * Overriden onResume
     * Marks the activity as active
     */
    @Override
    protected void onResume(){
        super.onResume();
        active = true;
    }

    /**
     * Marks the activity as inactive
     */
    @Override
    protected void onPause(){
        super.onPause();
        active = false;
    }

    /**
     * Marks the activity as inactive and removes the static reference to itself
     */
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


    /**
     * Creates a new {@link Message} using the information passed in.
     * On failure, notifies user with a Toast and returns null.
     * @param recipients    String containing phone numbers of recipients
     * @param messageText   String containing message content
     * @param state         Whether message is sent, received, or draft
     * @return              new {@link Message}, or null on failure
     */
    public Message createMessage(String recipients, String messageText, MessageState state) {
        if (messageText == null || messageText.length() == 0) {
            Toast.makeText(this,"Message is empty!",Toast.LENGTH_SHORT).show();
            return null;
        }

        if (recipients == null || recipients.length() == 0){
            Toast.makeText(this,"Recipient not selected!",Toast.LENGTH_SHORT).show();
            return null;
        }

        //not used but would allow multiple recipients if support added elsewhere
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


    /**
     * returns the EditText used for message editing
     * @return  message EditText
     */
    private EditText getMessageTextField() {
        return (EditText) findViewById(R.id.id_message_field);
    }

    /**
     * Sets the content of the message EditText
     * @param text  String used to set the message
     */
    private void setMessageText(String text) {
        getMessageTextField().setText(text, TextView.BufferType.EDITABLE);
    }


    /**
     * Obtains the content of the message from the message EditText
     * @return  message contained in EditText
     */
    public String getMessageText() {
        return getMessageTextField()
                .getText()
                .toString()
                .trim();
    }

    /**
     * Returns the EditText containing the recipient data
     * @return  EditText with recipient data
     */
    private EditText getMessageRecipientsField() {
        return (EditText) findViewById(R.id.id_phone_field);
    }

    /**
     * Sets the contents of the EditText for recipients
     * @param recipients    String used for the EditText
     */
    private void setMessageRecipients(String recipients) {
        getMessageRecipientsField().setText(recipients, TextView.BufferType.EDITABLE);
    }

    /**
     * Obtains the recipient phone numbers from the EditText as a String
     * @return  phone numbers of recipients
     */
    public String getMessageRecipients() {
        return getMessageRecipientsField()
                .getText()
                .toString()
                .trim();
    }

    /**
     * Obtains the recipient and message content and sends the message.
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