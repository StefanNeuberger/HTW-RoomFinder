package s53324_s53849.lsf_app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.gui.MainActivity;
import s53324_s53849.lsf_app.utils.AppContext;
import s53324_s53849.lsf_app.utils.Network;

import static java.security.AccessController.getContext;
import static s53324_s53849.lsf_app.provider.LSFContract.*;
public class LSFDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_INITIALIZED_KEY="databaseInitialized";

    private static final String DB_NAME = "veranstaltungen.db";
    private static int DB_VERSION = 1;

    private static volatile LSFDatabase sINSTANCE;
    private static final Object sLOCK = "";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Foreign key constraints are disabled by default by SQLite for backwards compatibility
        db.setForeignKeyConstraintsEnabled(true);
        createTables(db);
        // getAndInsertDataAsync(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(Veranstaltung.SQL_CREATE);
        db.execSQL(Raum.SQL_CREATE);
        db.execSQL(Gehoert_Zu.SQL_CREATE);
        db.execSQL(Termin.SQL_CREATE);
        db.execSQL(Gruppe.SQL_CREATE);
        db.execSQL(Studiengang.SQL_CREATE);
    }


    void insertData(InputStream insertStatements) {
        SQLiteDatabase db = getWritableDatabase();
        int numberOfStatements = 0;
        BufferedReader br = null;
        db.beginTransaction();
        try {
            br = new BufferedReader(new InputStreamReader(insertStatements));
            String sqlStatement;
            while ((sqlStatement = br.readLine()) != null) {
                if (sqlStatement.length() > 11) {
                    db.execSQL(sqlStatement);
                    numberOfStatements++;
                }
            }
            br.close();
            db.setTransactionSuccessful();

        } catch (IOException e) {
            try {
                br.close();
            } catch (NullPointerException | IOException ex) {
            }
        } finally {
            db.endTransaction();
        }
        if (numberOfStatements == 0) {
            throw new RuntimeException("Error while inserting data. Please try again.");
        }
    }

    private void insertInDb(SQLiteDatabase db, String assetFilePath) throws IOException {
        if (assetFilePath != null && assetFilePath.length() > 0) {
            SQLiteStatement statement = db.compileStatement(assetFilePath);
            statement.execute();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String table : tables) {
            db.execSQL("DROP TABLE IF EXISTS" + table);
        }
        onCreate(db);
    }

    public static LSFDatabase getInstance(Context context) {
        if (sINSTANCE == null) {
            synchronized (sLOCK) {
                if (context != null) {
                    sINSTANCE = new LSFDatabase(context);
                }
            }
        }
        return sINSTANCE;
    }

    private LSFDatabase(Context context) {
        //http://android-developers.blogspot.de/2009/01/avoiding-memory-leaks.html
        super(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        //Foreign key constraints are disabled by default by SQLite for backwards compatibility
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }



}
