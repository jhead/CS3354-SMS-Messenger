package cs3354group10.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import cs3354group10.messenger.activities.ThreadListActivity;
import cs3354group10.messenger.db.MessageDatabase;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public SMSBroadcastReceiver() {
    }


    /**
     * onReceive
     * receives broadcasts, only accepts SMS broadcast
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        Object[] sms = (Object[]) b.get("pdus");

        //catch when broadcast is not SMS
        if (sms == null)
            return;

        for (Object obj : sms) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);

            String body = message.getMessageBody();
            String sender = contactExists(message.getOriginatingAddress(),context);
            String str = "Sender: " + sender + "\n\n" + body;

            //send notifi

            //TODO - need to find contact in contact list, if not available then do this
            Contact c = new Contact(sender);

            //stick message in database
            MessageDatabase.insertMessage(context,new Message(c,body,MessageState.RECV));
        }
        ThreadListActivity.updateThreads();
    }

    private String contactExists( String number,Context context) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
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
}
