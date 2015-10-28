package cs3354group10.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

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

        if (sms == null)
            return;

        for (Object obj : sms) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);

            String body = message.getMessageBody();
            String sender = message.getOriginatingAddress();
            String str = "Sender: " + sender + "\n\n" + body;
            Toast t = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
