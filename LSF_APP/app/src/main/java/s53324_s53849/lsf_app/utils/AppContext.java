package s53324_s53849.lsf_app.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by User on 06.04.2017.
 */

public class AppContext extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        AppContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return AppContext.context;
    }

}
