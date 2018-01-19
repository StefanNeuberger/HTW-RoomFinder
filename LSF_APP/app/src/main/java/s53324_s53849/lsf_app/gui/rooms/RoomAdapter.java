package s53324_s53849.lsf_app.gui.rooms;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Locale;

import s53324_s53849.lsf_app.R;

/**
 * A custom cursor adapter to display the contents of a lecture.
 *
 * @see CursorAdapter
 */
public class RoomAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private int YELLOW, LIGHT_GREEN, GREEN, RED, ORANGE;

    public RoomAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mLayoutInflater = LayoutInflater.from(context);
        YELLOW = ResourcesCompat.getColor(context.getResources(), R.color.roomResultYellow, context.getTheme());
        LIGHT_GREEN = ResourcesCompat.getColor(context.getResources(), R.color.roomResultLightGreen, context.getTheme());
        GREEN = ResourcesCompat.getColor(context.getResources(), R.color.roomResultGreen, context.getTheme());
        RED = ResourcesCompat.getColor(context.getResources(), R.color.roomResultRed, context.getTheme());
        ORANGE = ResourcesCompat.getColor(context.getResources(), R.color.roomResultOrange, context.getTheme());
    }

    /**
     * Creates a new view from a predefined layout whose values will be set in bindView.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //creates a view from the given xml layout file
        View v = mLayoutInflater.inflate(R.layout.room_item, parent, false);
        return v;
    }

    /**
     * Sets the values of the given view.
     */
    @Override
    public void bindView(View view, Context context, Cursor c) {
        int zimmer_col = c.getColumnIndexOrThrow("zimmer");
        int gebaude_col = c.getColumnIndexOrThrow("gebaeude");
        int campus_col = c.getColumnIndexOrThrow("campus");
        int distance_col = c.getColumnIndexOrThrow("distance");
        int ende_davor_col = c.getColumnIndexOrThrow("ende_davor");
        String davorTime = c.getString(ende_davor_col);
        String davorLine = davorTime != null ? "End of last lecture: " + formatTime(davorTime) : "No lecture before on this day.";
        int distance = c.getInt(distance_col);
        distance = davorTime != null ? distance : -1;

        String displayText = "Room Nr. " + c.getString(zimmer_col) + "\n" + "Campus: " + c.getString(campus_col) + " " + c.getString(gebaude_col) + "\n" + davorLine;

        TextView textView = (TextView) view.findViewById(R.id.textView_Campus);
        textView.setBackgroundColor(getColor(distance));
        textView.setText(displayText);

    }

    private static final int HALF_HOUR = 30;
    private static final int ONE_HOUR = 2 * HALF_HOUR;

    private int getColor(int distanceToPreviousLecture) {

        if (distanceToPreviousLecture == -1) {
            return RED;
        }
        if (distanceToPreviousLecture < HALF_HOUR) {
            return GREEN;
        }
        System.out.println(distanceToPreviousLecture);
        if (distanceToPreviousLecture < HALF_HOUR + ONE_HOUR) {
            return LIGHT_GREEN;
        }
        if (distanceToPreviousLecture < 4 * ONE_HOUR) {
            return YELLOW;
        }
        if (distanceToPreviousLecture < 6 * ONE_HOUR) {
            return ORANGE;
        } else {
            return RED;
        }

    }

    private static String formatTime(String time) {
        time = String.format(Locale.ENGLISH, "%04d", Integer.parseInt(time));
        StringBuilder sb = new StringBuilder(time);
        sb.insert(2, ":");
        return sb.toString();
    }
}
