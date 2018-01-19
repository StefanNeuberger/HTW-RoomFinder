package s53324_s53849.lsf_app.database;

import android.content.Context;
import android.database.Cursor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import s53324_s53849.lsf_app.provider.ContentConsumer;
import s53324_s53849.lsf_app.provider.LSFContract;

public class LectureMapper {
    private static final String DELIMITER = "@";
    private static Map<String, String> lectureMap;
    private static String[] lectures;


    public static String[] getLectures(Context context) {
        //when the app is stopped all variables are lost
        if (lectureMap == null) {
            init(context);
        }
        return lectures;
    }

    /**
     * Called from the database initalizer the mapper.
     */
    static void init(Context c) {
        Cursor cursor = ContentConsumer.getAllStudiengangs(c);
        if (cursor.getCount() < 100) {
            throw new RuntimeException("Error in init of LectureMapper. Database probably not initalized.");
        }
        initMap(cursor);
        lectures = lectureMap.keySet().toArray(new String[]{});
    }

    public static List<String> map(List<String> args) {
        LinkedList<String> res = new LinkedList<>();
        for (String arg : args) {
            String fullName = lectureMap.get(arg);
            if (fullName != null) {
                res.add(fullName);
            }
        }
        return res;
    }

    private static void initMap(Cursor c) {
        TreeSet<String> studiengangs = getStudiengangsWithNewestPruefungsOrdnung(c);
        lectureMap = new TreeMap<>();
        for (String studiengang : studiengangs) {
            //TODO remove this some day, hardcoded last minute
            if (!getSimpleName(studiengang).equals("Angewandte Informatik")) {
                lectureMap.put(getSimpleName(studiengang), studiengang);
            }
        }

    }

    private static TreeSet<String> getStudiengangsWithNewestPruefungsOrdnung(Cursor c) {
        TreeSet<String> set = new TreeSet<>(entryComparator);
        c.moveToFirst();
        do {
            int id_col = c.getColumnIndexOrThrow(LSFContract.Studiengang._ID);
            String entry = c.getString(id_col);
            set.add(entry);
        } while (c.moveToNext());
        return set;
    }

    private static int getPruefungsOrdnung(String studiengang) {
        try {
            String tmp = studiengang.split(DELIMITER)[1];
            return Integer.parseInt(tmp.trim());
        } catch (Exception e) {
            System.out.println(studiengang);
            throw e;
        }
    }

    private static String getSimpleName(String studiengang) {
        return studiengang.split(DELIMITER)[0];
    }

    private static Comparator<String> entryComparator = new Comparator<String>() {
        @Override
        public int compare(String course1, String course2) {
            String name1 = course1.split(DELIMITER)[0];
            String name2 = course2.split(DELIMITER)[0];
            if (!name1.equals(name2)) {
                return name1.compareTo(name2);
            } else {
                int pruefOrd1 = getPruefungsOrdnung(course1);
                int pruefOrd2 = getPruefungsOrdnung(course2);
                if (pruefOrd1 > pruefOrd2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    };
}
