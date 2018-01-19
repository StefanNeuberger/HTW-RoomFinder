package lsfRetriever.lsfLectures.utils;

import provider.LSFContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataHelper {
    static {
        Map<String, String> tage = new HashMap<>();
        String[] t = {"Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"};
        for (int i = 0; i < t.length; i++) {
            int v = i + 1;
            tage.put(t[i], v + "");
        }
        DataHelper.tage = tage;
    }

    //map from days to their corresponding int values(int values stored in strings)
    private static Map<String, String> tage;

    /**
     * Converts a String from the format "DD.MM.YYYY" to YYYYMMDD
     */
    public static String convertDate(String date) {
        String[] s = date.split("\\.");
        return "'" + s[2] + s[1] + s[0] + "'";
    }

    public static String getTagIntValue(String tag) {
        String val = tage.get(tag);
        if (val != null) {
            return val;
        } else {
            throw new RuntimeException("Unknown day" + tag);
        }
    }

    /**
     * Returns the int corresponding to the given type enum.
     */
    public static String getTypIntValue(String typ, String date) {

        //wöch, aber dem Umlat vertraue ich nicht,
        if (typ.contains("ch")) {
            return LSFContract.Typ.EVERY_WEEK.code + "";
        }
        if (typ.contains("unger.")) {
            return LSFContract.Typ.ODD_WEEK.code + "";
        }
        if (typ.contains("ger.")) {
            return LSFContract.Typ.EVEN_WEEK.code + "";
        }
        if (typ.contains("Einzelt")) {
            return LSFContract.Typ.SINGLE.code + "";
        }
        System.out.println("NOTICE:Unknown TYP for: " + typ + " parsing date..");
        return getType(date).code + "";
    }

    /**
     * Returns the type(odd or even week) of the given date
     */
    private static LSFContract.Typ getType(String date) {
        date = date.replace('\'', ' ').trim();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.GERMANY);
            Date d = format.parse(date);
            Calendar myDate = format.getCalendar();
            myDate.setTime(d);
            int week = myDate.get(Calendar.DAY_OF_WEEK);
            //the weeks are zero indexed so we add 1
            return ((week + 1) % 2 == 0) ? LSFContract.Typ.EVEN_WEEK : LSFContract.Typ.ODD_WEEK;

        } catch (ParseException e) {
            throw new RuntimeException("Parse exception at: " + date);
        }
    }

}
