package s53324_s53849.lsf_app.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.Arrays;

import s53324_s53849.lsf_app.database.LSFDatabase;
import s53324_s53849.lsf_app.database.query.LectureQueryBuilder;
import s53324_s53849.lsf_app.database.query.LectureQueryData;
import s53324_s53849.lsf_app.database.query.RoomQueryBuilder;


public class LSFProvider extends ContentProvider {

    private static final LSFUriMatcher sUriMatcher = new LSFUriMatcher();

    private LSFDatabase mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = LSFDatabase.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDatabase.getReadableDatabase();
        LSFUriEnum match = sUriMatcher.match(uri);

        if (match == null) {
            return null;
        }
        if (match == LSFUriEnum.SEARCH_LECTURE) {
            LectureQueryData data = LectureQueryData.readFromStringArray(selectionArgs);
            String query = new LectureQueryBuilder(data).buildQuery();
            return db.rawQuery(query, data.getQueryData());
        } else if (match == LSFUriEnum.SEARCH_ROOM) {
            String query = RoomQueryBuilder.buildRoomQuery(selectionArgs.length);
            System.out.println(query);
            return db.rawQuery(query, selectionArgs);
        }
        return db.query(match.table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public String getType(Uri uri) {
        return sUriMatcher.match(uri).contentType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Insert not supported.");
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        throw new UnsupportedOperationException("Bulk Insert not supported.");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Delete not supported.");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Update not supported.");
    }
}