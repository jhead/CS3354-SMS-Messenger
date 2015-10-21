package cs3354group10.messenger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cs3354group10.messenger.Message;
import cs3354group10.messenger.MessageThread;

public class MessageDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PATH = "Messenger.db";

    public MessageDatabaseHelper(Context context) {
        super(context, DATABASE_PATH, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create threads table
        db.execSQL(
            "CREATE TABLE " + MessageThread.DB_TABLE_NAME + " (" +
            MessageThread.DB_COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            MessageThread.DB_COLUMN_NAME_CONTACT + " VARCHAR(255)" +
            ")"
        );

        // Create messages table
        db.execSQL(
            "CREATE TABLE " + Message.DB_TABLE_NAME + " (" +
            Message.DB_COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            Message.DB_COLUMN_NAME_CONTACT + " VARCHAR(255)" +
            Message.DB_COLUMN_NAME_TEXT + " TEXT" +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }


}
