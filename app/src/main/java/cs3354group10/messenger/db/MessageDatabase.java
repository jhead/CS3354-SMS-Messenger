package cs3354group10.messenger.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cs3354group10.messenger.Contact;
import cs3354group10.messenger.Message;

public class MessageDatabase {

    protected static SQLiteOpenHelper getDatabaseHelper(Context context) {
        return MessageDatabaseHelper.getInstance(context);
    }

    protected static SQLiteDatabase getReadableDatabase(Context context) {
        return getDatabaseHelper(context).getReadableDatabase();
    }

    protected static SQLiteDatabase getWritableDatabase(Context context) {
        return getDatabaseHelper(context).getWritableDatabase();
    }

    /**
     * Query the database to build a list of message threads, including each contact name and the
     * text from the latest message from that contact.
     *
     * @param context
     * @return A cursor to iterate the list of threads
     */
    public static Cursor queryThreads(Context context) {
        SQLiteDatabase db = getReadableDatabase(context);

        String groupBy = Message.DB_COLUMN_NAME_CONTACT;
        String orderBy = Message.DB_COLUMN_NAME_TIMESTAMP + " DESC";
        return db.query(Message.DB_TABLE_NAME, Message.DB_COLUMNS, null, null, groupBy, null, orderBy, null);
    }


    /**
    * Query the database for a list of messages by a specific contact.
    *
    * @param context
    * @param contact Messages between the user and this contact will be returned.
    * @return A cursor to iterate the list of messages.
    */
    public static Cursor queryMessages(Context context, Contact contact) {
        SQLiteDatabase db = getReadableDatabase(context);

        String selection = Message.DB_COLUMN_NAME_CONTACT + " = ?";
        String[] selectionArgs = new String[] { contact.getName() };
        //String groupBy = Message.DB_COLUMN_NAME_CONTACT;

        String orderBy = Message.DB_COLUMN_NAME_TIMESTAMP + " ASC";
        return db.query(Message.DB_TABLE_NAME, Message.DB_COLUMNS, selection, selectionArgs, null, null, orderBy, null);
        //return db.query(Message.DB_TABLE_NAME, Message.DB_COLUMNS, selection, null, groupBy, null, orderBy, null);
    }


    /**
     * Inserts a new message into the database
     *
     * @param context
     * @param message Message to be inserted
     * @return ID of the new row
     */
    public static long insertMessage(Context context, Message message) {
        SQLiteDatabase db = getWritableDatabase(context);

        return db.insert(Message.DB_TABLE_NAME, null, message.getFields());
    }

}
