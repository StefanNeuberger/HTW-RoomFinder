package s53324_s53849.lsf_app.gui.rooms;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.gui.AutoHideNavigationBarActivity;
import s53324_s53849.lsf_app.provider.ContentConsumer;


public class ResultActivityRoomSearch extends AutoHideNavigationBarActivity {

    private String datum, campus, gebaeude, startZeit, endZeit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_search_result);

        final Bundle extras = getIntent().getExtras();

        if (extras != null) {
            campus = extras.getString(RoomSearchActivity.CAMPUS);
            gebaeude = extras.getString(RoomSearchActivity.GEBAEUDE);
            datum = extras.getString(RoomSearchActivity.DATUM);
            startZeit = extras.getString(RoomSearchActivity.START_TIME);
            endZeit = extras.getString(RoomSearchActivity.END_TIME);
            query();

        }
    }

    private void query() {
        Cursor c = ContentConsumer.getFreeRooms(getApplicationContext(), startZeit, endZeit, datum, campus, gebaeude);
        if (c.getCount() == 0) {
            Toast.makeText(this, "Keine Räume gefunden.", Toast.LENGTH_LONG).show();
        } else {

            //create an adapter to wrap the content of the cursor
            CursorAdapter adapter = new RoomAdapter(getApplicationContext(), c, true);

            //sets the adapter whose content to display in the listView
            ListView listView = (ListView) findViewById(R.id.room_list_view);
            System.out.println(adapter.getCount());
            listView.setAdapter(adapter);
        }
    }

    public static String formatTime(String time) {
        time = String.format(Locale.ENGLISH, "%04d", Integer.parseInt(time));
        StringBuilder sb = new StringBuilder(time);
        sb.insert(2, ":");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "" +
                "date='" + datum + '\'' +
                ", campus='" + campus + '\'' +
                ", building='" + gebaeude + '\'' +
                ", start ='" + formatTime(startZeit) + '\'' +
                ", end='" + formatTime(endZeit) + '\'';
    }
}
