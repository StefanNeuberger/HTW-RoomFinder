package s53324_s53849.lsf_app.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The contract class for the provider. According to the Android tutorial a contract class's purpose is as follows:
 * "A contract class defines constants that help applications work with the content URIs, column names,
 * intent actions, and other features of a content provider."
 * As a guidance the google sample provider has been used which can be viewed here:
 * <a href="https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/provider/ScheduleContract.java">ScheduleContract</a>
 */
public class LSFContract {
    public static void main(String args[]) throws IOException {
        //null out BASE_CONTENT_URI uri before starting
        writeSchemaToFile("../DB/schema.sql");
    }

    //for convenience remove before production
    private static void writeSchemaToFile(String path) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(path));
        writer.println("PRAGMA foreign_keys = on;");
        writer.println(Veranstaltung.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.println(Raum.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.println(Gehoert_Zu.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.println(Termin.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.println(Gruppe.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.println(Studiengang.SQL_CREATE.replaceAll("[ \t]+", " "));
        writer.close();
    }

    public enum Typ {
        SINGLE(0),
        ODD_WEEK(1),
        EVEN_WEEK(2),
        EVERY_WEEK(3);

        public final int code;

        Typ(int code) {
            this.code = code;
        }
    }

    /**
     * The authority of the content provider. It is written in the manifest and used to uniquely define
     * the content provider and to reference it.
     */
    public static final String PROVIDER_AUTHORITY = "s53324_s53849.lsf_app.provider";

    /**
     * The base baseUri of the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + PROVIDER_AUTHORITY);

    /**
     * An array contains the names of the Tables.
     */
    public static final String[] tables = {Studiengang.TABLE_NAME, Gehoert_Zu.TABLE_NAME,
            Veranstaltung.TABLE_NAME, Gruppe.TABLE_NAME, Termin.TABLE_NAME, Raum.TABLE_NAME};


    public interface StudiengangColumns extends BaseColumns {
        /**
         * The name of the Studiengang is stored in the _id attribute. For example 'Angewandte Informatik (B) Immatrikulation ab WS 2012'
         */

    }

    public interface Studiengang extends StudiengangColumns {
        String TABLE_NAME = "studiengang";
        String BASE_URI = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + _ID + "  TEXT PRIMARY KEY ON CONFLICT IGNORE\n" +
                "                 NOT NULL\n" +
                ");";
    }

    public interface TerminColumns {
        String DOZENT = "dozent";
        /**
         * The day of the week (e.g. Monday) on which this termin takes place.
         * Stored as an int which corresponds to an {@link java.util.Calendar} constant.
         * Days of week are indexed starting with Sunday at 1
         */
        String TAG = "tag";
        /**
         * The starting date stored as an integer in the form of YYYYMMDD for easy and fast comparison operators
         */
        String START_DATUM = "start_datum";
        /**
         * The end date stored as an integer in the form of YYYYMMDD for easy and fast comparison operators
         */
        String END_DATUM = "end_datum";
        /**
         * The type of the Termin which is an int value corresponding to a {@link Typ}
         */
        String TYP = "typ";
        /**
         * The starting time of this termin stored as an integer in the format HHMM for fast comparisons
         */
        String BEGIN = "begin";
        /**
         * The ending time of this termin stored as an integer in the format HHMM for fast comparisons
         */
        String ENDE = "ende";
        String HINWEIS = "hinweis";
        String GEHALTEN_IN = "gehalten_in";
        String GEHOERT_ZU_GRUPPE = "gehoert_zu_gr";
    }

    public interface Termin extends TerminColumns, BaseColumns {
        String TABLE_NAME = "termin";
        String BASE_URI = TABLE_NAME;
        /**
         * The content baseUri used to reference the table.
         */
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + _ID + "           INTEGER PRIMARY KEY \n" +
                "                          NOT NULL,\n" +
                "    " + DOZENT + "        TEXT    NOT NULL,\n" +
                "    " + TAG + "           INTEGER NOT NULL,\n" +
                "    " + BEGIN + "         INTEGER NOT NULL,\n" +
                "    " + ENDE + "          INTEGER NOT NULL,\n" +
                "    " + HINWEIS + "       TEXT            ,\n" +
                "    " + START_DATUM + "   TEXT    NOT NULL,\n" +
                "    " + END_DATUM + "     TEXT    NOT NULL,\n" +
                "    " + TYP + "           INTEGER    NOT NULL,\n" +
                "    " + GEHOERT_ZU_GRUPPE + " INTEGER REFERENCES " + Gruppe.TABLE_NAME + " (" + Gruppe._ID + ") ON DELETE CASCADE,\n" +
                "    " + GEHALTEN_IN + " INTEGER REFERENCES " + Raum.TABLE_NAME + " (" + Raum._ID + ") ON DELETE RESTRICT,\n" +
                "    UNIQUE (\n" +
                "        " + _ID + ",\n" +
                "        " + GEHOERT_ZU_GRUPPE + "\n" +
                "    )\n" +
                "    ON CONFLICT ABORT\n" +
                ");";
    }

    public interface GruppeColumns {
        String GEHOERT_ZU_VERANSTALTUNG = "gehoert_zu_ver";
        String NAME = "name";
    }

    public interface Gruppe extends GruppeColumns, BaseColumns {
        String TABLE_NAME = "gruppe";
        String BASE_URI = TABLE_NAME;
        /**
         * The content baseUri used to reference the table.
         */
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + _ID + "            INTEGER PRIMARY KEY\n" +
                "                           NOT NULL,\n" +
                "    " + NAME + "           TEXT    NOT NULL,\n" +
                "    " + GEHOERT_ZU_VERANSTALTUNG + " INTEGER REFERENCES " + Veranstaltung.TABLE_NAME + " (" + Veranstaltung._ID + ") ON DELETE CASCADE\n" +
                "                           NOT NULL,\n" +
                "    UNIQUE (\n" +
                "        " + NAME + ",\n" +
                "        " + GEHOERT_ZU_VERANSTALTUNG + "\n" +
                "    )\n" +
                "    ON CONFLICT ABORT\n" +
                ");";
    }

    public interface Gehoert_ZuColumns {

        /**
         * id des Studienganges zu dem er gehört
         */
        String STUDIENGANG_ID = "studiengang_id";

        /**
         * semester: z.B 4
         */
        String MIN_SEMESTER = "min_semester";
        String MAX_SEMESTER = "max_semester";
        String VERANSTALTUNG_ID = "ver_id";

    }

    public interface Gehoert_Zu extends Gehoert_ZuColumns, BaseColumns {

        String TABLE_NAME = "gehoert_zu";
        String BASE_URI = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);
        /**
         * SQL Anweisung zur Schemadefinition
         */
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + VERANSTALTUNG_ID + "   INTEGER REFERENCES " + Veranstaltung.TABLE_NAME + " (" + Veranstaltung._ID + ") ON DELETE CASCADE\n" +
                "                     NOT NULL,\n" +
                "    " + STUDIENGANG_ID + "  TEXT REFERENCES " + Studiengang.TABLE_NAME + " (" + Studiengang._ID + ") ON DELETE CASCADE\n" +
                "                     NOT NULL,\n" +
                "    " + MIN_SEMESTER + " INTEGER,\n" +
                "    " + MAX_SEMESTER + " INTEGER,\n" +
                "    " + _ID + "      INTEGER PRIMARY KEY AUTOINCREMENT\n" +
                "                     NOT NULL,\n" +
                "    UNIQUE (\n" +
                "        " + VERANSTALTUNG_ID + ",\n" +
                "        " + STUDIENGANG_ID + "\n" +
                "    )\n" +
                "    ON CONFLICT IGNORE\n" +
                ");";
    }


    public interface VeranstaltungColumns {

        /**
         * Pflichtfeld, not null
         * z.B Besteuerung der nationalen Unternehmenstätigkeit
         */
        String VER_NAME = "ver_name";

        /**
         * Deutsch, Englisch usw...
         */
        String SPRACHE = "sprache";
        String URL = "url";
    }

    public interface Veranstaltung extends VeranstaltungColumns, BaseColumns {
        String TABLE_NAME = "veranstaltung";
        String BASE_URI = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);

        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + _ID + "      INTEGER PRIMARY KEY\n" +
                "                     NOT NULL,\n" +
                "    " + SPRACHE + "  TEXT    NOT NULL,\n" +
                "    " + VER_NAME + " TEXT    NOT NULL,\n" +
                "    " + URL + "      TEXT    NOT NULL\n" +
                ");";
    }

    public interface RaumColumns {

        /**
         * Primary key
         * ID := Abkürzung campus + Gebäude + RaumNr. (z.B. WHC444)
         */

        /**
         * gebaudename: z.B. C
         */
        String GEBAEUDE = "gebaeude";

        /**
         * Raumnummer: z.B 444
         */
        String ZIMMER = "zimmer";

        /**
         * Campusabkürzung: z.B. WH fuer Wilhelminenhof
         */
        String CAMPUS = "campus";
    }

    public interface Raum extends RaumColumns, BaseColumns {
        String TABLE_NAME = "raum";
        String BASE_URI = TABLE_NAME;
        Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, BASE_URI);
        //primary key aus geb, zimmer und campus berechnen
        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + "(\n" +
                "    " + _ID + "      TEXT PRIMARY KEY ON CONFLICT IGNORE \n" +
                "                     NOT NULL,\n" +
                "    " + GEBAEUDE + " TEXT    NOT NULL,\n" +
                "    " + ZIMMER + "   TEXT    NOT NULL,\n" +
                "    " + CAMPUS + "   TEXT    NOT NULL\n" +
                ");";
    }
}
