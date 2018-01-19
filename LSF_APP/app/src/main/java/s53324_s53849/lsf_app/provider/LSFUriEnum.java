package s53324_s53849.lsf_app.provider;

import android.net.Uri;

import static s53324_s53849.lsf_app.provider.LSFContract.*;
import static s53324_s53849.lsf_app.provider.LSFContract.PROVIDER_AUTHORITY;

/**
 * An enum used to wrap the URIs of the tables. This class should be exposed to client applications
 * alongside the {@link LSFContract} class .
 */
public enum LSFUriEnum {
    STUDIENGANG(100, Studiengang.BASE_URI, Studiengang.TABLE_NAME),
    TERMIN(200, Termin.BASE_URI, Termin.TABLE_NAME),
    GRUPPE(300, Gruppe.BASE_URI, Gruppe.TABLE_NAME),
    GEHOERT_ZU(401, Gehoert_Zu.BASE_URI, Gehoert_Zu.TABLE_NAME),
    VERANSTALTUNG(500, Veranstaltung.BASE_URI, Veranstaltung.TABLE_NAME),
    RAUM(600, Raum.BASE_URI, Raum.TABLE_NAME),

    SEARCH_ROOM(1000, "search_rooms", null),
    SEARCH_LECTURE(2000, "search_lecture", null);

    public final String contentType;
    public final int code;
    public final String table;
    public final String baseUri;

    LSFUriEnum(int code, String baseUri, String table) {
        this.baseUri = baseUri;
        this.code = code;
        this.table = table;
        this.contentType = makeContentType(baseUri, false);
    }

    private static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + PROVIDER_AUTHORITY + ".";

    private static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + PROVIDER_AUTHORITY + ".";

    /**
     * Returns the mime type of a given item.
     *
     * @param id   the id of the item. Ex "studiengang"
     * @param item true if it is an item(a row), false if it is directory(a table)
     */
    private static String makeContentType(String id, boolean item) {
        if (id == null) {
            return null;
        }
        return item ? CONTENT_ITEM_TYPE_BASE + id : CONTENT_TYPE_BASE + id;
    }

    public static Uri getUri(LSFUriEnum lsfUriEnum) {
        if (lsfUriEnum == null) {
            throw new IllegalArgumentException("given uriEnum can't be null.");
        }
        return Uri.withAppendedPath(LSFContract.BASE_CONTENT_URI, lsfUriEnum.baseUri);
    }
}
