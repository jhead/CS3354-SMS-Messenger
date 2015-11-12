package cs3354group10.messenger;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class Contact {

    protected String name;
    public static ArrayList<Contact> contactList = new ArrayList<>();


    // TODO: additional contact information
    public Contact(String name) {
        this.name = name;
        contactList.add(this);
    }

    public String getName() {
        return this.name;
    }

    public static String resolveName(Context context, String number) {
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

    public static String resolveNumber(Context context, String name) {
        Cursor cur = context
                .getContentResolver()
                .query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                        new String[]{name},
                        null
                );

        try {
            if (cur.moveToFirst()) {
                String number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                return number;
            }
        } finally {
            if (cur != null)
                cur.close();
        }

        return name; //when name is number (when contact doesnt exist)
    }


}
