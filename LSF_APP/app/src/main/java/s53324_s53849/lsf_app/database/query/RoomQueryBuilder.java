package s53324_s53849.lsf_app.database.query;

public class RoomQueryBuilder {
    private static final int ARGS_COUNT = 15;

    private static final String PROJECTION = "r.* , max(ende) as ende_davor, min ((?/100 * 60.0 + ? % 100) -( ende/100 * 60 + ende % 100)) as distance, (zimmer/100) as etage";
    private static final int MIN_DISPLAY_COUNT = 5;

    public static String buildRoomQuery(int selectionArgsLength) {
        if (selectionArgsLength != ARGS_COUNT) {
            throw new RuntimeException("WRONG ARGS COUNT EXPECTED: " + ARGS_COUNT + " ACTUAL: " + selectionArgsLength);
        }
        return "select " + PROJECTION + " from ( select room.* from  ( SELECT raum._id as _id, campus, gebaeude, zimmer FROM raum WHERE (gebaeude=? AND campus=?)" +
                "EXCEPT SELECT raum._id as _id, campus, gebaeude, zimmer FROM termin join raum on termin.gehalten_in=raum._id" +
                " WHERE ( (typ=3 or typ=0 or typ=?) AND start_datum <= ? AND end_datum >= ? AND termin.begin < ? AND termin.ende > ? AND termin.tag = ?) ) " +
                "as room join termin on termin.gehalten_in=room._id group by room._id, room.campus,room.gebaeude, room.zimmer having count(*) >=" + MIN_DISPLAY_COUNT + " )as r left outer join termin on" +
                " termin.gehalten_in=r._id and termin.ende< ? and (typ=3 or typ=0 or typ=?) and termin.tag=? and start_datum <= ? and end_datum >= ? group by r._id, r.campus, r.gebaeude, r.zimmer order by distance IS NULL asc, distance asc";
    }

}