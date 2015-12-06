package cs3354group10.messenger;

import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * Defines method to visually organize texts by sender in {@link cs3354group10.messenger.activities.ThreadViewActivity}.
 * Changes background and orientation of TextView depending on sender or draft status.
 * @see cs3354group10.messenger.activities.ThreadViewActivity
 */
public class ThreadViewBinder implements SimpleCursorAdapter.ViewBinder {
    public ThreadViewBinder(){
        super();
    }

    /**
     * Sets the TextViews in {@link cs3354group10.messenger.activities.ThreadViewActivity} to have custom color
     * and orientation.
     * @param view          TextView that will be modified
     * @param cursor        Cursor containing message nformation
     * @param columnIndex   Not used
     * @return  returns true
     */
    public boolean setViewValue (View view, Cursor cursor, int columnIndex){
        TextView t = (TextView) view;
        t.setText(cursor.getString(cursor.getColumnIndex(Message.DB_COLUMN_NAME_TEXT)));

        int ms = cursor.getInt(cursor.getColumnIndex(Message.DB_COLUMN_NAME_STATE));

        if (ms == MessageState.SENT.getValue()) {
            t.setBackgroundColor(Color.argb(255, 180, 255, 180));
            t.setGravity(Gravity.RIGHT);
            t.setMinWidth(10000);
        }
        else if (ms == MessageState.RECV.getValue()){
            t.setBackgroundColor(Color.argb(255, 180, 180, 255));
            t.setGravity(Gravity.LEFT);
            t.setMinWidth(10000);
            
        }
        else{
            t.setBackgroundColor(Color.argb(255, 255, 180, 180));
            t.setGravity(Gravity.RIGHT);
            t.setMinWidth(10000);
        }
        t.setTextSize(20);
        t.setMinHeight(100);
        return true;
    }
}
