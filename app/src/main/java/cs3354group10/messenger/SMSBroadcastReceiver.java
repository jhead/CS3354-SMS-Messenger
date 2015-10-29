package cs3354group10.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;
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
            String sender = message.getOriginatingAddress();
            String str = "Sender: " + sender + "\n\n" + body;

            //temporary message display
            Toast t = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            t.show();

            //TODO - need to find contact in contact list, if not available then do this
            Contact c = new Contact(sender);

            //stick message in database
            MessageDatabase.insertMessage(context,new Message(c,body,MessageState.RECV));
        }
    }
}
