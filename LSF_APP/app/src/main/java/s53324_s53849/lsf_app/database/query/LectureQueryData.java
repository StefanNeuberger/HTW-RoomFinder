package s53324_s53849.lsf_app.database.query;

import android.widget.Button;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import s53324_s53849.lsf_app.database.LectureMapper;

import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.*;

public class LectureQueryData implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String AWE_TEXT = "AWE-Module";
    public static final String SONDERVERANSTALTUNGEN_TEXT = "Sonderveranstaltungen";

    //used when "serializing" the lectureQueryData as a String[]
    private static final String SEPARATOR = "@SEPARATOR@";

    /**
     * Contains the Keys to the data hold by the LectureQueryData objects.
     * START_TIME,END_TIME and WEEKDAY are obligatory Field and if not set will be automatically initialized.
     * TYP, SEMESTER and INCLUDE_AWE are compulsory and will be left null if not initialized.
     * INCLUDE_AWE and INCLUDE_SONDERVERANSTALT act as booleans. If they are not null they are set.
     */
    public interface Keys {
        int START_TIME = 0;
        int END_TIME = 1;
        int WEEKDAY = 2;

        int TYP = 3;
        int SEMESTER = 4;

        int INCLUDE_AWE = 5;
        int INCLUDE_SONDERVERANSTALT = 6;
    }

    private String[] attributes = new String[7];

    private List<String> studiengangs = new LinkedList<>();

    private Calendar calendar = Calendar.getInstance();

    public String getAttribute(int id) {
        if (id < 0 || id > 7) {
            throw new IllegalArgumentException("id must be one of Keys constants");
        }
        initAttributeIfRequired(id);
        return attributes[id];
    }

    private void initAttributeIfRequired(int id) {
        switch (id) {
            case START_TIME:
                setStartTime(attributes[START_TIME]);
                break;
            case END_TIME:
                setEndTime(attributes[END_TIME]);
                break;
            case WEEKDAY:
                setWeekday(attributes[WEEKDAY]);
                break;
            default:
                break;
        }
    }

    public List<String> getMappedStudiengangs() {
        return LectureMapper.map(studiengangs);
    }

    public boolean atleastOneStudiengangSelected() {
        return studiengangs.size() > 0 || attributes[INCLUDE_AWE] != null || attributes[INCLUDE_SONDERVERANSTALT] != null;
    }

    public void setStartTime(String startTime) {
        if (startTime == null) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            startTime = (String.format(Locale.ENGLISH, "%02d", hour) + String.format(Locale.ENGLISH, "%02d", minute));
        }
        attributes[START_TIME] = startTime;
    }

    public void setEndTime(String endTime) {
        if (endTime == null) {
            int start = Integer.parseInt(attributes[START_TIME]);
            int end = start <= 2200 ? start + 200 : 2400;
            endTime = String.format(Locale.ENGLISH, "%04d", end);
        }
        attributes[Keys.END_TIME] = endTime;
    }

    public void setWeekday(String weekday) {
        attributes[Keys.WEEKDAY] = weekday == null ? calendar.get(Calendar.DAY_OF_WEEK) + "" : weekday + "";
    }

    public void setStudiengangs(List<String> studiengangs) {
        attributes[Keys.INCLUDE_AWE] = studiengangs.remove(AWE_TEXT) ? "" : null;
        attributes[Keys.INCLUDE_SONDERVERANSTALT] = studiengangs.remove(SONDERVERANSTALTUNGEN_TEXT) ? "" : null;
        this.studiengangs = studiengangs;
    }

    public void setTyp(int typ) {
        attributes[Keys.TYP] = typ <= 0 ? null : typ + "";
    }

    public void setSemester(int semester) {
        attributes[Keys.SEMESTER] = semester <= 0 ? null : semester + "";
    }

    public String[] getQueryData() {
        LinkedList<String> tmpList = new LinkedList<>();
        addToList(tmpList, START_TIME);
        addToList(tmpList, END_TIME);
        addToList(tmpList, WEEKDAY);
        tmpList.addAll(getMappedStudiengangs());
        addToList(tmpList, SEMESTER);
        addToList(tmpList, SEMESTER);
        addToList(tmpList, TYP);
        return tmpList.toArray(new String[]{});
    }

    private void addToList(LinkedList<String> list, int id) {
        String attr = getAttribute(id);
        if (attr != null) {
            list.add(attr);
        }
    }

    public String[] writeAsStringArray() {
        LinkedList<String> tmpList = new LinkedList<>(Arrays.asList(attributes));
        tmpList.add(SEPARATOR);
        tmpList.addAll(studiengangs);
        return tmpList.toArray(new String[]{});
    }

    public static LectureQueryData readFromStringArray(String[] lectureQueryDataAsStringArray) {
        LectureQueryData lectureQueryData = new LectureQueryData();
        //Arrays.asList returns an abstract list which throws an exception when add is called..
        LinkedList<String> dataAsLinkedList = new LinkedList<>(Arrays.asList(lectureQueryDataAsStringArray));

        int separator = dataAsLinkedList.indexOf(SEPARATOR);
        lectureQueryData.attributes = dataAsLinkedList.subList(0, separator).toArray(new String[]{});
        lectureQueryData.studiengangs = dataAsLinkedList.subList(separator, dataAsLinkedList.size());
        return lectureQueryData;
    }

    public static String formatTime(String time) {
        time = String.format(Locale.ENGLISH, "%04d", Integer.parseInt(time));
        return time.substring(0, 2) + ":" + time.substring(2, time.length());
    }
}
