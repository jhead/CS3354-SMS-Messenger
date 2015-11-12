package cs3354group10.messenger;

import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * ThreadViewBinder
 * Defines method to visually organize texts by sender in ThreadViewActivity
 * Created by Satsuki on 11/10/2015.
 */
public class ThreadViewBinder implements SimpleCursorAdapter.ViewBinder {
    public ThreadViewBinder(){
        super();
    }

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
        return true;
    }
}
