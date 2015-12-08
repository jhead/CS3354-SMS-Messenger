package cs3354group10.messenger;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Provides a basic object for storing contact details in memory while rendering threads and
 * messages on-screen.
 */
public class Contact {

    protected String name;

    protected static ArrayList<Contact> contactList = new ArrayList<>();

    // TODO: additional contact information
    public Contact(String name) {
        this.name = name;
        contactList.add(this);
    }

    public static ArrayList<Contact> getContacts() {
        return new ArrayList<>(contactList);
    }

    /**
     * Provides the contact name.
     * @return Contact name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Translates a phone number to a contact name. If the phone number is not in the user's contact
     * list, this method returns the phone number provided.
     *
     * @param context Current activity or application context
     * @param number Phone number to resolve
     * @return Contact name or phone number (fallback)
     */
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

    /**
     * Translates a contact name to a phone number.
     *
     * @param context Current activity or application context
     * @param name Contact name to resolve
     * @return Phone number or contact name (fallback)
     */
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
