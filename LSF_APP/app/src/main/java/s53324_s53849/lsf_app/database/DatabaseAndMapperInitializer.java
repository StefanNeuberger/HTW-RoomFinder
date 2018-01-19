package s53324_s53849.lsf_app.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.utils.Network;


public class DatabaseAndMapperInitializer {
    public static boolean running;

    private static void setRunning(boolean running) {
        synchronized (DatabaseAndMapperInitializer.class) {
            DatabaseAndMapperInitializer.running = running;
        }
    }

    public static void initialize(View v) {
        synchronized (DatabaseAndMapperInitializer.class) {
            if (!running) {
                setRunning(true);
                new DatabaseAndMapperInitializerTask(v).execute(INSERT_STATEMENTS_URL);
            }
        }
    }

    private static final String POST_DATA = "password=mabeleg7";
    private static final String INSERT_STATEMENTS_URL = "https://webdrive.htw-berlin.de/public/file/78FAgfV4jkaSp2bJBZJ4jg/insert_data_no_dups.sql" + "?" + POST_DATA;


    public static boolean databaseInitializationSuccessful(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean(LSFDatabase.DATABASE_INITIALIZED_KEY, false);
    }

    private static class DatabaseAndMapperInitializerTask extends AsyncTask<String, Object, Object> {
        private Context context;
        private String errorMessage;

        private View callBack;

        private DatabaseAndMapperInitializerTask(View callBackView) {
            context = callBackView.getContext().getApplicationContext();
            this.callBack = callBackView;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (databaseInitializationSuccessful(context)) {
                callBack.callOnClick();
            } else {
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
            setRunning(false);
        }

        @Override
        protected Object doInBackground(String[] url) {
            try {
//                InputStream insertStatements = Network.connectOrThrowIfNoNetworkConnection(context, url[0]);
                InputStream insertStatements = context.getResources().openRawResource(R.raw.insert_data_no_dups);
                LSFDatabase.getInstance(context).insertData(insertStatements);
                LectureMapper.init(context);
                writeToPreferences();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
            return null;
        }

        private void writeToPreferences() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(LSFDatabase.DATABASE_INITIALIZED_KEY, true);
            editor.commit();
        }

    }
}
