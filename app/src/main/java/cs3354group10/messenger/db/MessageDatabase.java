package cs3354group10.messenger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;

public class MessageDatabase {

    private static Context context;
    private static MessageDatabaseHelper dbHelper;

    /**
     * The context must be set before any other actions. Without the context, the
     * MessageDatabaseHelper cannot be created and the database cannot be accessed.
     *
     * @param ctx Context (via getContext())
     */
    public static void setContext(Context ctx) {
        context = ctx;
        dbHelper = new MessageDatabaseHelper(context);
    }

    /**
     * Query the database for a list of messages by contact.
     *
     * @param contact Messages between the user and this contact will be returned.
     * @return A list of matching messages.
     */
    public static List<Message> queryMessages(Contact contact) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // TODO

        return new LinkedList<Message>();
    }

}
