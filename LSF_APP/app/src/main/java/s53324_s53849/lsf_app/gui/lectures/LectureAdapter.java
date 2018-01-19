package s53324_s53849.lsf_app.gui.lectures;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.gui.rooms.RoomSearchActivity;
import s53324_s53849.lsf_app.provider.LSFContract;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * A custom cursor adapter to display the contents of a lecture.
 *
 * @see CursorAdapter
 */
public class LectureAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;


    public LectureAdapter(Context context, Cursor cursor, boolean autoRequery) {
        super(context, cursor, autoRequery);
        mLayoutInflater = LayoutInflater.from(context);
    }

    /**
     * Creates a new view from a predefined layout whose values will be set in bindView.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //creates a view from the given xml layout file
        View v = mLayoutInflater.inflate(R.layout.lecture_item, parent, false);
        return v;
    }

    /**
     * Sets the values of the given view.
     */
    @Override
    public void bindView(View view, final Context context, Cursor c) {
        int ver_name_col = c.getColumnIndexOrThrow("ver_name");
        int studiengang_col = c.getColumnIndexOrThrow("studiengang_id");
        int min_semester_col = c.getColumnIndexOrThrow("min_semester");
        int max_semester_col = c.getColumnIndexOrThrow("max_semester");
        int url_col = c.getColumnIndexOrThrow("url");
        int start_time_col = c.getColumnIndexOrThrow(LSFContract.Termin.BEGIN);
        int end_time_col = c.getColumnIndexOrThrow(LSFContract.Termin.ENDE);

        String studiengang = c.getString(studiengang_col);//<br> - html line break
        String studiengDisplayString = studiengang == null ? "" : "Studiengang: " + studiengang.split("@")[0] + "<br>";

        String min_semester = c.getString(min_semester_col);
        String max_semester = c.getString(max_semester_col);
        String semesterDisplayString = getSemesterDisplayString(min_semester, max_semester);


        String start_time = c.getString(start_time_col);
        String end_time = c.getString(end_time_col);
        String timeDisplayString = "Zeit: " + formatTime(start_time) + " - " + formatTime(end_time) + "<br>";

        String urlDisplayString = "<a href='" + c.getString(url_col) + "'<u>more</u></a>";

        TextView textView = (TextView) view.findViewById(R.id.textView_Lecture);
        textView.setText(Html.fromHtml(c.getString(ver_name_col) + "<br>" + studiengDisplayString
                + semesterDisplayString + timeDisplayString + urlDisplayString));

        textView.setLinksClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private String getSemesterDisplayString(String min_semester, String max_semester) {
        if (max_semester == null || Integer.parseInt(max_semester) == 100) {
            return "";
        }
        if (min_semester.equals(max_semester)) {
            return "Semester: " + min_semester + "<br>";
        }
        return "Semester: " + min_semester + " - " + max_semester + "<br>";

    }

    public static String formatTime(String time) {
        time = String.format(Locale.ENGLISH, "%04d", Integer.parseInt(time));
        return time.substring(0, 2) + ":" + time.substring(2, time.length());
    }
}
