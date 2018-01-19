package s53324_s53849.lsf_app.database.query;


import android.database.sqlite.SQLiteQueryBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import s53324_s53849.lsf_app.provider.LSFContract;

import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.INCLUDE_AWE;
import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.INCLUDE_SONDERVERANSTALT;
import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.SEMESTER;
import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.TYP;

public class LectureQueryBuilder {
    public static final String[] projection = {"veranstaltung._id", "ver_name", "studiengang_id", "min_semester",
            "max_semester", "url", LSFContract.Termin.BEGIN, LSFContract.Termin.ENDE};

    private static String joinTables = "veranstaltung left join gehoert_zu on veranstaltung._id=ver_id" +
            " left join studiengang on studiengang._id=studiengang_id left join gruppe on" +
            " gehoert_zu_ver=veranstaltung._id left join termin on termin.gehoert_zu_gr=gruppe._id left" +
            " join raum on gehalten_in=raum._id";
    private LectureQueryData data;
    private SQLiteQueryBuilder queryBuilder;

    public LectureQueryBuilder(LectureQueryData data) {
        this.data = data;
    }

    public String buildQuery() {
        queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(joinTables);
        queryBuilder.setDistinct(true);
        queryBuilder.appendWhere(" termin.begin >= ? and termin.ende <=? and tag=?  ");

        appendOptionalWhere();
        String query = queryBuilder.buildQuery(projection, null, null, null, null, null);
        System.out.println(query);
        return query;
    }

    private void appendOptionalWhere() {
        appendOptionalStudiengang();
        appendOptionalSemester();
        appendOptionalTyp();
    }

    private void appendOptionalStudiengang() {

        LinkedList<String> studiengangWhere = new LinkedList<>();
        int studiengangsCount = data.getMappedStudiengangs().size();

        for (int i = 0; i < studiengangsCount; i++) {
            studiengangWhere.add(" studiengang._id= ? ");
        }
        if (data.getAttribute(INCLUDE_SONDERVERANSTALT) != null) {
            studiengangWhere.add(" studiengang._id ISNULL ");
        }
        String formatedStudiengangWhere = formatStudiengangWhere(studiengangWhere);
        queryBuilder.appendWhere(formatedStudiengangWhere);

    }

    private String formatStudiengangWhere(List<String> studiengangStatements) {
        if (studiengangStatements.isEmpty()) {
            return "and ver_name LIKE '%AWE%'";

        } else {
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterator = studiengangStatements.iterator();
            for (int i = 0; i < studiengangStatements.size() - 1; i++) {
                sb.append(iterator.next());
                sb.append(" or ");
            }
            sb.append(iterator.next());
            return "and ( " + sb.toString() + getAWE() + " )";
        }
    }

    private void appendOptionalSemester() {
        if (data.getAttribute(SEMESTER) != null) {
            queryBuilder.appendWhere(" and (( min_semester<=? or min_semester ISNULL )and (max_semester >= ? or max_semester ISNULL) ) ");
        }
    }

    private void appendOptionalTyp() {
        if (data.getAttribute(TYP) != null) {
            queryBuilder.appendWhere(" and typ = ? ");
        }
    }

    private String getAWE() {
        boolean includeAWE = data.getAttribute(INCLUDE_AWE) != null;
        return includeAWE ? " or ver_name LIKE '%AWE%' " : "and ver_name NOT LIKE '%AWE%' ";
    }

}
