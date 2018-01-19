package lsfRetriever.lsfLectures;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lsfRetriever.lsfLectures.utils.DataHelper;
import lsfRetriever.lsfLectures.utils.IOHelper;
import lsfRetriever.lsfLectures.utils.StringHelper;
import provider.LSFContract.*;

public class DATA {
    private static final Object sLOCK = new Object();

    private Pattern getRaumPattern = Pattern.compile("^([A-Z]{2}) ([A-Z]{1}) ([0-9]{3}) - (.*)$");
    private Pattern getTerminPattern = Pattern.compile("^.*(Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag).*\\|(.*)-(.*)\\|(.*)\\|.*:(.*)\\|.*:(.*)$");

    private static final String VERANSTALTUNG_PATH = "tables/veranstaltung_Data.sql";
    private static final String STUDIENGANG_PATH = "tables/studiengang_Data.sql";
    private static final String GEHOERT_ZU_PATH = "tables/gehoert_zu_Data.sql";
    private static final String RAUM_PATH = "tables/raum_Data.sql";
    private static final String GRUPPE_PATH = "tables/gruppen_Data.sql";
    private static final String TERMIN_PATH = "tables/termin_Data.sql";
    private static final String[] FILE_PATHS =
            {VERANSTALTUNG_PATH, STUDIENGANG_PATH, GEHOERT_ZU_PATH, RAUM_PATH, GRUPPE_PATH, TERMIN_PATH};
    public static final String MERGED_DATA_PATH = "tables/merged/insert_data.sql";

    private static String currentStudiengangID = "";
    private static int currentGruppeID = 0;
    private static int currentTerminID = 0;
    private static String currentStringRaumID = "";
    private static int currentGehoert_ZuID = 0;
    private static int currentVeranstaltungID = 0;

    private String name;
    private String sprache;
    private String information;
    private String url;
    private String[] studiengangUndSemester;
    private List<Map<String, String>> gruppen;

    public DATA(String url, String name, String sprache, String information, String[] studiengangUndSemester,
                List<Map<String, String>> gruppen) {
        super();
        this.name = StringHelper.formatSQLString(name);
        this.sprache = StringHelper.formatSQLString(sprache);
        this.url = StringHelper.formatSQLString(url);
        this.information = StringHelper.formatSQLString(information);
        this.studiengangUndSemester = studiengangUndSemester;
        this.gruppen = gruppen;
    }

    public boolean writeInsertTableToFile() throws IOException {
        synchronized (sLOCK) {
            // VERANSTALTUNG
            write(VERANSTALTUNG_PATH, veranstaltungInsertStatement());

            // STUDIENGANG UND GEHOERT_ZU
            writeStudiengangUndGehoertZu();

            // TERMIN, RAUM, UND GRUPPE
            for (int i = 0; i < gruppen.size(); i++) {

                // RAUM
                String raumLine = gruppen.get(i).get("Raum&nbsp;");
                Matcher m2 = getRaumPattern.matcher(raumLine);
                if (m2.find()) {
                    String campusKurz = m2.group(1);
                    String gebaeude = m2.group(2);
                    String raum = m2.group(3);
                    String campusLang = m2.group(4);
                    currentStringRaumID = ("'" + campusKurz + gebaeude + raum + "'");
                    campusKurz = ("'" + m2.group(1) + "'");
                    gebaeude = ("'" + m2.group(2) + "'");
                    raum = ("'" + m2.group(3) + "'");
                    campusLang = ("'" + m2.group(4) + "'");
                    write(RAUM_PATH, raumInsertStatement(gebaeude, raum, campusKurz));
                }

                // GRUPPE
                String zugnummer = String.valueOf(i + 1);
                write(GRUPPE_PATH, gruppeInsertStatement(zugnummer));

                // TERMIN
                String dozRaw = gruppen.get(i).get("Dozent");
                String dozent = StringHelper.formatSQLString(dozRaw == null ? gruppen.get(i).get("Dozenten") : dozRaw);
                String hinweis = StringHelper.formatSQLString(gruppen.get(i).get("Hinweise zum Termin"));
                String dateInfoLine = gruppen.get(i).get("Termin");
                Matcher m3 = getTerminPattern.matcher(dateInfoLine);

                if (m3.find()) {
                    String tag = m3.group(1);

                    String timeStart = StringHelper.removeWhitespacesAndColons(m3.group(2));
                    String timeEnd = StringHelper.removeWhitespacesAndColons(m3.group(3));
                    String typ = ("'" + m3.group(4) + "'");

                    String startDatum = StringHelper.removeWhitespaces(m3.group(5));
                    String endDatum = StringHelper.removeWhitespaces(m3.group(6));
                    write(TERMIN_PATH, terminInsertStatement(dozent, tag, timeStart, timeEnd, typ, startDatum, endDatum, hinweis));
                }

            }
            return true;
        }
    }

    private void writeStudiengangUndGehoertZu() {

        for (String current : studiengangUndSemester) {
            //if there is no information about the semester and studiengang the course can be take by anyone. null
            //can be used to represent any studiengang so no need create a entry
            if (current.equals("")) {
                break;
            }
            String[] args = current.split("\\|");
            String studiengang = args[0];
            if (args.length == 1) {
                System.out.println("NOTICE WRITING " + studiengang + " as STUDIENGANG. NO MIN/MAX SEMESTER GIVEN. WHOLE LINE: " + current);
                studiengang = StringHelper.formatSQLString(studiengang);
                write(STUDIENGANG_PATH, studiengangInsertStatement(studiengang));
                write(GEHOERT_ZU_PATH, gehoertZuInsertStatement("1", "100"));
                break;
            }
            String semester = args[1];
            studiengang = StringHelper.formatSQLString(studiengang);
            //STUDIENGANG
            write(STUDIENGANG_PATH, studiengangInsertStatement(studiengang));
            String[] semesterMinMax = getSemesterMinMax(semester);
            // GEHOERT_ZU
            write(GEHOERT_ZU_PATH, gehoertZuInsertStatement(semesterMinMax[0], semesterMinMax[1]));
        }
    }


    /**
     * Extracts the min and max semester from the semester key(e.g. 1. - 6. Semester).
     * if semester contains the string "jedes semester" 1 and 100 are returned, which match any semester
     * if there is only 1 digit-(e.g 3. Semester) then it is returned as min and max as well (3,3)
     * otherwise an exception is thrown
     */
    private String[] getSemesterMinMax(String semester) {

        if (semester.contains("jedes semester")) {
            //there won't be any courses with 100 semester
            return new String[]{"1", "100"};
        }
        String[] toReturn = new String[2];
        int r_index = 0;
        char[] chars = semester.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (Character.isDigit(c)) {
                toReturn[r_index] = c + "";
                i++;
                if (i != chars.length && Character.isDigit(chars[i])) {
                    toReturn[r_index] += chars[i];
                }
                r_index = r_index + 1;
            }
        }
        if (toReturn[0] == null) {
            //System.out.println("NOTICE while parsing studiengang for: " + semester + " wrote as any.");
            return new String[]{"1", "100"};
        }//only 1 int has been found
        if (toReturn[1] == null) {
            toReturn[1] = toReturn[0];
        }
        return toReturn;
    }


    public static void deleteFilesIfExist() {
        for (String filePath : FILE_PATHS) {
            File f = new File(filePath);
            f.delete();
        }
        File merged = new File("tables/merged");
        merged.mkdirs();
    }

    public static void writeMergedFiles() {
        merge(MERGED_DATA_PATH, FILE_PATHS);
    }

    private static void merge(String outputFilename, String... filePaths) {
        System.out.println("merged");
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)))) {
            for (String f : filePaths) {
                BufferedReader br = new BufferedReader(new FileReader(new File(f)));
                while (br.ready()) {
                    out.println(br.readLine());
                }

                br.close();
                out.println();
                out.println();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write(String file, String text) {
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(text);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public String studiengangInsertStatement(String studiengang) {
        currentStudiengangID = formatStudiengang(studiengang);
        String insertStatement = String.format("INSERT INTO %s (%s) VALUES(%s);", Studiengang.TABLE_NAME,
                Studiengang._ID, currentStudiengangID);
        return insertStatement;
    }

    private String formatStudiengang(String studiengang) {
        String formatted = studiengang.replaceAll(", PrüfungsOrdnung ", "@");
        return formatted;
    }

    public String veranstaltungInsertStatement() {
        currentVeranstaltungID++;
        String insertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES( %s, %s, %s, %s);",
                Veranstaltung.TABLE_NAME, Veranstaltung._ID, Veranstaltung.SPRACHE, Veranstaltung.VER_NAME, Veranstaltung.URL,
                currentVeranstaltungID, sprache, name, url);
        return insertStatement;
    }

    public String raumInsertStatement(String gebaeude, String raum, String campus) {
        String insertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES( %s, %s, %s, %s);",
                Raum.TABLE_NAME, Raum._ID, Raum.GEBAEUDE, Raum.ZIMMER, Raum.CAMPUS, currentStringRaumID, gebaeude, raum, campus);
        return insertStatement;
    }

    public String gruppeInsertStatement(String zugnummer) {
        currentGruppeID++;
        String insertStatement = String.format("INSERT INTO %s (%s, %s, %s) VALUES(%d, %s, %d);", Gruppe.TABLE_NAME,
                Gruppe._ID, Gruppe.NAME, Gruppe.GEHOERT_ZU_VERANSTALTUNG, currentGruppeID, zugnummer,
                currentVeranstaltungID);
        return insertStatement;
    }

    public String terminInsertStatement(String dozent, String tag, String begin, String ende, String typ, String startDatum, String endDatum, String hinweis) {
        startDatum = DataHelper.convertDate(startDatum);
        endDatum = DataHelper.convertDate(endDatum);
        tag = DataHelper.getTagIntValue(tag);
        typ = DataHelper.getTypIntValue(typ, startDatum);
        currentTerminID++;
        String insertStatement = String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"
                        + "VALUES(%d, %s, %s, %s, %s, %s, %s, %s, %s, %d, %s);",
                Termin.TABLE_NAME, Termin._ID, Termin.DOZENT, Termin.TAG, Termin.BEGIN, Termin.ENDE, Termin.TYP,
                Termin.START_DATUM, Termin.END_DATUM, Termin.HINWEIS, Termin.GEHOERT_ZU_GRUPPE, Termin.GEHALTEN_IN,
                currentTerminID, dozent, tag, begin, ende, typ, startDatum, endDatum, hinweis, currentGruppeID,
                currentStringRaumID);
        return insertStatement;
    }

    public String gehoertZuInsertStatement(String minSemester, String maXsemester) {
        currentGehoert_ZuID++;
        String insertStatement = String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES(%s, %s, %s, %s, %s);", Gehoert_Zu.TABLE_NAME,
                Gehoert_Zu._ID, Gehoert_Zu.STUDIENGANG_ID, Gehoert_Zu.VERANSTALTUNG_ID, Gehoert_Zu.MIN_SEMESTER, Gehoert_Zu.MAX_SEMESTER,
                currentGehoert_ZuID, currentStudiengangID, currentVeranstaltungID, minSemester, maXsemester);
        return insertStatement;
    }

    @Override
    public String toString() {
        return "DATA [name=" + name + ", sprache=" + sprache + ", information=" + information
                + ", coursesOfStudyAndSemesters=" + Arrays.toString(studiengangUndSemester) + ", \ngruppen=" + gruppen
                + "]";
    }

}
