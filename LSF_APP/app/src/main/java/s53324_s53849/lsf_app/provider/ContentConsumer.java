package s53324_s53849.lsf_app.provider;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import s53324_s53849.lsf_app.database.query.LectureQueryData;

public class ContentConsumer {

    /**
     * returns all free Rooms in the given interval on the given day.
     *
     * @param context  the context of the calling app
     * @param start    the starting time in the Format "HHMM"
     * @param end      the ending time in the Format "HHMM"
     * @param day      the day in the Format"YYYYMMDD"
     * @param campus   the campus
     * @param building the building
     * @throws SecurityException if the calling context doesn't have permissions to read the content provider.
     */
    public static Cursor getFreeRooms(Context context, String start, String end, String day, String campus, String building) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.GERMANY);
        try {
            Date date = format.parse(day);
            Calendar myDate = format.getCalendar();
            myDate.setTime(date);
            String tag = myDate.get(Calendar.DAY_OF_WEEK) + "";

            String typ = getTyp(myDate) + "";
            Uri searchRoomsUri = LSFUriEnum.getUri(LSFUriEnum.SEARCH_ROOM);
            String[] selectionArgs = new String[]{start,start,building, campus, typ, day, day, end, start, tag, start, typ, tag, day, day};

            return context.getContentResolver().query(searchRoomsUri, null, null, selectionArgs, null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getTyp(Calendar myDate) {
        int week = myDate.get(Calendar.WEEK_OF_MONTH);
        //the weeks are zero indexed so we add 1
        return ((week + 1) % 2 == 0) ? LSFContract.Typ.EVEN_WEEK.code : LSFContract.Typ.ODD_WEEK.code;
    }

    public static CursorLoader getLecturesLoader(Context context, LectureQueryData data) {
        Uri searchLectureURI = LSFUriEnum.getUri(LSFUriEnum.SEARCH_LECTURE);
        String[] serializedData = data.writeAsStringArray();

        return new CursorLoader(context, searchLectureURI, null, null, serializedData, null);
    }

    public static Cursor test(Context context) {
        return context.getContentResolver().query(LSFUriEnum.getUri(LSFUriEnum.RAUM), null, null, null, null, null);
    }

    public static Cursor getAllStudiengangs(Context context) {
        return context.getContentResolver().query(LSFUriEnum.getUri(LSFUriEnum.STUDIENGANG), null, null, null, null, null);
    }
}
