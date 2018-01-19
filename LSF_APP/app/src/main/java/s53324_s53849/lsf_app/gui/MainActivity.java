package s53324_s53849.lsf_app.gui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;



import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.database.DatabaseAndMapperInitializer;
import s53324_s53849.lsf_app.gui.lectures.LectureSearchActivity;
import s53324_s53849.lsf_app.gui.rooms.RoomSearchActivity;


public class MainActivity extends AutoHideNavigationBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_main);
    }

    public void onClickRoomSearch(View roomButton) {
        initDBIfNecessaryAndStartActivity(roomButton, RoomSearchActivity.class);
    }

    private void initDBIfNecessaryAndStartActivity(View view, Class activityClass) {
        if (DatabaseAndMapperInitializer.databaseInitializationSuccessful(getApplicationContext())) {
            final Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        } else {
            DatabaseAndMapperInitializer.initialize(view);
        }
    }

    public void onClickLectureSearch(View lectureButton) {
        initDBIfNecessaryAndStartActivity(lectureButton, LectureSearchActivity.class);
    }

    private void hideStatusBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


}

