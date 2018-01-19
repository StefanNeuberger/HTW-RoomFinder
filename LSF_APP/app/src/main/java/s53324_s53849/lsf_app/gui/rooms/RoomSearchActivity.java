package s53324_s53849.lsf_app.gui.rooms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Locale;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.gui.AutoHideNavigationBarActivity;
import s53324_s53849.lsf_app.gui.MyTimePickerDialog;

public class RoomSearchActivity extends AutoHideNavigationBarActivity {

    private String queryDate = "";
    private String queryStartTime = "";
    private String queryEndTime = "";
    String queryCampus = "";
    String queryGebaeude = "";

    static Calendar calendar = Calendar.getInstance();
    private Button btn_date;
    private Button btn_start_time;
    private Button btn_end_time;
    private Button btn_campus;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    public static final String CAMPUS = "campus";
    public static final String GEBAEUDE = "gebaeude";
    public static final String DATUM = "datum";
    public static final String START_TIME = "starttime";
    public static final String END_TIME = "endtime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_search);

        startTimePickerDialog = new MyTimePickerDialog(this, startTimePickerListener, calendar, "Start");
        endTimePickerDialog = new MyTimePickerDialog(this, endTimePickerListener, calendar, "End");

        btn_date = (Button) findViewById(R.id.Button_DatePicker);
        btn_start_time = (Button) findViewById(R.id.Button_startTime);
        btn_end_time = (Button) findViewById(R.id.Button_endTime);
        btn_campus = (Button) findViewById(R.id.Button_campus);

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });

        btn_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStartTime();
            }
        });

        btn_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEndTime();
            }
        });

        // Campus und Gebäude
        btn_campus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(RoomSearchActivity.this);
                View adView = getLayoutInflater().inflate(R.layout.double_dialog_spinner, null);
                adBuilder.setTitle("");
                final Spinner adSpinnerCampus = (Spinner) adView.findViewById(R.id.spinner_dialogSpinner_1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RoomSearchActivity.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Campus_Spinner_Entries));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adSpinnerCampus.setAdapter(adapter);

                final Spinner adSpinnerGeb = (Spinner) adView.findViewById(R.id.spinner_dialogSpinner_2);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RoomSearchActivity.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Gebaeude_Spinner_Entries));
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adSpinnerGeb.setAdapter(adapter2);

                adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!adSpinnerCampus.getSelectedItem().toString().equalsIgnoreCase("Wähle Campus…") &&
                                !adSpinnerGeb.getSelectedItem().toString().equalsIgnoreCase("Wähle Gebäude…")) {
                            queryCampus = adSpinnerCampus.getSelectedItem().toString();
                            queryGebaeude = adSpinnerGeb.getSelectedItem().toString();
                            btn_campus.setText("Campus: " + queryCampus + "\nGebäude: " + queryGebaeude);
                            dialog.dismiss();
                        }
                    }
                });

                adBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                adBuilder.setView(adView);
                AlertDialog dialog = adBuilder.create();
                dialog.show();
            }
        });
    }

    private void updateDate() {

        new DatePickerDialog(this, datePickerListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateStartTime() {
        startTimePickerDialog.show();
    }

    private void updateEndTime() {
        endTimePickerDialog.show();
    }

    private String getCurrentTime() {
        return getCurrentTime(0);
    }

    private String getCurrentTime(int hourOffset) {
        String string_hour = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.HOUR_OF_DAY) + hourOffset);
        String string_minute = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MINUTE));
        return string_hour + string_minute;
    }

    private String getCurrentDate() {
        String string_Year = String.valueOf(calendar.get(Calendar.YEAR));
        String string_Month = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.MONTH) + 1);
        String string_Day = String.format(Locale.ENGLISH, "%02d", calendar.get(Calendar.DAY_OF_MONTH));
        return string_Year + string_Month + string_Day;
    }

    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String string_Year = String.valueOf(year);
            String string_Month = String.format("%02d", month + 1);
            String string_Day = String.format("%02d", dayOfMonth);
            String btnDate = string_Day + "." + string_Month + "." + string_Year;
            queryDate = string_Year + string_Month + string_Day;
            btn_date.setText(btnDate);
        }
    };

    TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String string_hour = String.format("%02d", hourOfDay);
            String string_minute = String.format("%02d", minute);
            String btnStartTime = string_hour + ":" + string_minute;
            queryStartTime = string_hour + string_minute;
            btn_start_time.setText(btnStartTime);
        }
    };

    TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String string_hour = String.format("%02d", hourOfDay);
            String string_minute = String.format("%02d", minute);
            String btnEndTime = string_hour + ":" + string_minute;
            queryEndTime = string_hour + string_minute;
            btn_end_time.setText(btnEndTime);
        }
    };

    public void onClickFindRoom(View button) {

        //campus
        if (queryCampus.equals("")) {
            Toast.makeText(RoomSearchActivity.this, "Bitte Wählen Sie Campus und Gebäude!", Toast.LENGTH_LONG).show();
            return;
        }
        final String campus = queryCampus;

        //gebaeude
        if (queryGebaeude.equals("")) {
            Toast.makeText(RoomSearchActivity.this, "Bitte Wählen Sie Campus und Gebäude!", Toast.LENGTH_LONG).show();
            return;
        }
        final String gebaeude = queryGebaeude;

        // datum
        final String datum = queryDate.equals("") ? getCurrentDate() : queryDate;
        queryDate = datum;
        btn_date.setText(formatDate(queryDate));

        //startTime
        if (queryStartTime.equals("")) {
            queryStartTime = getCurrentTime();
        }
        final String startTime = queryStartTime;
        btn_start_time.setText(formatTime(queryStartTime));

        //endTime
        if (queryEndTime.equals("")) {
            int intStartTime = Integer.valueOf(startTime);
            int intEndTime;
            intEndTime = (intStartTime >= 2300) ? 2400 : intStartTime + 100;
            queryEndTime = String.format("%04d", intEndTime);
        }
        final String endTime = queryEndTime;
        btn_end_time.setText(formatTime(queryEndTime));

        if (Integer.valueOf(queryStartTime) > Integer.valueOf(queryEndTime)) {
            Toast.makeText(RoomSearchActivity.this, "Achtung! Startzeit liegt nach Endzeit", Toast.LENGTH_SHORT).show();
            return;
        }

        // Übergabe an ResultActivity
        final Intent intent = new Intent(this, ResultActivityRoomSearch.class);

        intent.putExtra(CAMPUS, campus);
        intent.putExtra(GEBAEUDE, gebaeude);
        intent.putExtra(DATUM, datum);
        intent.putExtra(START_TIME, startTime);
        intent.putExtra(END_TIME, endTime);


        startActivity(intent);
    }

    private String formatTime(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.insert(2, ':');
        return sb.toString();
    }

    private String formatDate(String s) {
        char[] cA = new char[8];
        StringBuilder sb = new StringBuilder(s);
        sb.getChars(6, 8, cA, 0);
        sb.getChars(4, 6, cA, 2);
        sb.getChars(0, 4, cA, 4);
        String tmp = "";
        for (int i = 0; i < cA.length; i++) {
            tmp += cA[i];
        }
        sb = new StringBuilder(tmp);
        sb.insert(2, '.');
        sb.insert(5, '.');
        return sb.toString();
    }
}
