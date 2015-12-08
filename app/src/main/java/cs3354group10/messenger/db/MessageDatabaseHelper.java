package cs3354group10.messenger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cs3354group10.messenger.Message;

/**
 * SQLiteOpenHelper instance required to work with SQLite databases.
 *
 * @see cs3354group10.messenger.db.MessageDatabase
 */
public class MessageDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Current database schema version.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * Path to SQLite database file.
     */
    public static final String DATABASE_PATH = "Messenger.db";

    private static MessageDatabaseHelper instance;

    private MessageDatabaseHelper(Context context) {
        super(context, DATABASE_PATH, null, DATABASE_VERSION);
    }

    /**
     * MessageDatabaseHelper singleton
     *
     * @param context Current activity or application context
     * @return an instance of MessageDatabaseHelper
     */
    public static MessageDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MessageDatabaseHelper(context);
        }

        return instance;
    }

    /**
     * Sets up the SQLite database schema for new databases.
     *
     * Do not explicitly call this method. It is handled internally.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create messages table
        db.execSQL(
            "CREATE TABLE " + Message.DB_TABLE_NAME + " (" +
            Message.DB_COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Message.DB_COLUMN_NAME_CONTACT + " VARCHAR(255)," +
            Message.DB_COLUMN_NAME_TIMESTAMP + " TIMESTAMP," +
            Message.DB_COLUMN_NAME_TEXT + " TEXT," +
            Message.DB_COLUMN_NAME_STATE + " INT(1)" +
            ")"
        );
    }

    /**
     * Currently unused.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }


}
