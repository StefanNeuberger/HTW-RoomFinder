package s53324_s53849.lsf_app.gui.lectures;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import s53324_s53849.lsf_app.R;
import s53324_s53849.lsf_app.database.LectureMapper;
import s53324_s53849.lsf_app.database.query.LectureQueryData;
import s53324_s53849.lsf_app.gui.AutoHideNavigationBarActivity;
import s53324_s53849.lsf_app.gui.MyTimePickerDialog;

import static s53324_s53849.lsf_app.database.query.LectureQueryData.Keys.*;


public class LectureSearchActivity extends AutoHideNavigationBarActivity {

    private LectureQueryData queryData = new LectureQueryData();
    Calendar calendar = Calendar.getInstance();
    private Button btn_start_time;
    private Button btn_end_time;
    private Button btn_Studiengang_Select;
    private Button btn_semester;
    private Button btn_typ;
    private Button btn_wochentag;

    TimePickerDialog startTimePickerDialog;
    TimePickerDialog endTimePickerDialog;

    private String[] studiengangArray;
    private boolean[] checkedStudiengang;

    private String[] wochentage;
    private ArrayList<Integer> selectedUserItems = new ArrayList<>();

    private String[] getStudiengangArray() {
        List<String> lectures = new LinkedList<>(Arrays.asList(LectureMapper.getLectures(getApplicationContext())));
        lectures.add(0, LectureQueryData.AWE_TEXT);
        lectures.add(0, LectureQueryData.SONDERVERANSTALTUNGEN_TEXT);
        return lectures.toArray(new String[]{});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_search);

        btn_semester = (Button) findViewById(R.id.Button_semester);
        btn_wochentag = (Button) findViewById(R.id.Button_wochentag);
        btn_typ = (Button) findViewById(R.id.Button_typ);
        btn_Studiengang_Select = (Button) findViewById(R.id.Button_selectStudiengang);

        studiengangArray = getStudiengangArray();
        checkedStudiengang = new boolean[studiengangArray.length];
        wochentage = getResources().getStringArray(R.array.Wochentag_Spinner_Entries);

        startTimePickerDialog = new MyTimePickerDialog(this, startTimePickerListener, calendar, "Start");
        endTimePickerDialog = new MyTimePickerDialog(this, endTimePickerListener, calendar, "End");

        //Studiengang
        btn_Studiengang_Select.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder studiengangBuilder = new AlertDialog.Builder(LectureSearchActivity.this);
                studiengangBuilder.setTitle("Studiengänge");
                studiengangBuilder.setMultiChoiceItems(studiengangArray, checkedStudiengang, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            selectedUserItems.add(position);
                        } else {
                            selectedUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });
                studiengangBuilder.setCancelable(false);
                studiengangBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String item = "";
                        List<String> selected = new LinkedList<String>();
                        for (int i = 0; i < selectedUserItems.size(); i++) {
                            String selectedItem = studiengangArray[selectedUserItems.get(i)];
                            item += selectedItem;
                            selected.add(selectedItem);
                            if (i != selectedUserItems.size() - 1) {
                                item += ",";
                            }
                        }
                        queryData.setStudiengangs(selected);
                        btn_Studiengang_Select.setText(item);
                        btn_Studiengang_Select.setTextSize(10);
                        if (selectedUserItems.size() == 0) {
                            btn_Studiengang_Select.setText(R.string.Button_studiengang);
                        }
                    }
                });


                studiengangBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                studiengangBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checkedStudiengang.length; i++) {
                            checkedStudiengang[i] = false;
                            selectedUserItems.clear();
                            queryData.setStudiengangs(new LinkedList<String>());
                            btn_Studiengang_Select.setTextSize(15);
                            btn_Studiengang_Select.setText(R.string.Button_studiengang);
                        }
                    }
                });

                AlertDialog studiengangDialog = studiengangBuilder.create();
                studiengangDialog.show();
            }
        }));

        btn_start_time = (Button) findViewById(R.id.Button_startTimeLectureSearch);
        btn_end_time = (Button) findViewById(R.id.Button_endTimeLectureSearch);

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


        // SEMESTER
        btn_semester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(LectureSearchActivity.this);
                View adView = getLayoutInflater().inflate(R.layout.simple_dialog_spinner, null);
                adBuilder.setTitle("Semester");

                final Spinner adSpinnerSemester = (Spinner) adView.findViewById(R.id.spinner_simpledialogSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LectureSearchActivity.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.SemesterVon_Spinner_Entries));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adSpinnerSemester.setAdapter(adapter);

                adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String semester = adSpinnerSemester.getSelectedItem().toString();
                        if (!semester.contains("Alle")) {
                            queryData.setSemester(Integer.parseInt(semester));
                        }
                        btn_semester.setText(semester);
                        dialog.dismiss();
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

        // WOCHENTAG
        btn_wochentag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(LectureSearchActivity.this);
                View adView = getLayoutInflater().inflate(R.layout.simple_dialog_spinner, null);
                adBuilder.setTitle("Wochentag");
                final Spinner adSpinnerWochentag = (Spinner) adView.findViewById(R.id.spinner_simpledialogSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LectureSearchActivity.this,
                        android.R.layout.simple_spinner_item, wochentage);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adSpinnerWochentag.setAdapter(adapter);

                adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int weekday = adSpinnerWochentag.getSelectedItemPosition();

                        String weekdayAsString = weekday == 0 ? null : weekday + "";
                        queryData.setWeekday(weekdayAsString);
                        btn_wochentag.setText(adSpinnerWochentag.getSelectedItem().toString());
                        dialog.dismiss();
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

        // TYP
        btn_typ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(LectureSearchActivity.this);
                View adView = getLayoutInflater().inflate(R.layout.simple_dialog_spinner, null);
                adBuilder.setTitle("TYP");
                final Spinner adSpinnerTyp = (Spinner) adView.findViewById(R.id.spinner_simpledialogSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LectureSearchActivity.this,
                        android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.Typ_Spinner_Entries));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adSpinnerTyp.setAdapter(adapter);

                adBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        queryData.setTyp(adSpinnerTyp.getSelectedItemPosition());
                        btn_typ.setText(adSpinnerTyp.getSelectedItem().toString());
                        dialog.dismiss();
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

    private void updateStartTime() {
        startTimePickerDialog.show();
    }

    private void updateEndTime() {
        endTimePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener startTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String string_hour = String.format("%02d", hourOfDay);
            String string_minute = String.format("%02d", minute);
            String btnStartTime = string_hour + ":" + string_minute;
            queryData.setStartTime(string_hour + string_minute);

            btn_start_time.setText(btnStartTime);
        }
    };

    TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String string_hour = String.format("%02d", hourOfDay);
            String string_minute = String.format("%02d", minute);
            String btnEndTime = string_hour + ":" + string_minute;
            queryData.setEndTime(string_hour + string_minute);
            btn_end_time.setText(btnEndTime);
        }
    };

    public void onClickFindLecture(View button) {
        if (!queryData.atleastOneStudiengangSelected()) {
            Toast.makeText(LectureSearchActivity.this, "Bitte wählen Sie mindestens einen Studiengang aus", Toast.LENGTH_LONG).show();
            return;
        }
        setButtonTextsIfNecessary();
        final Intent intent = new Intent(this, LectureSearchResultActivity.class);
        intent.putExtra("data", queryData);
        startActivity(intent);
    }

    private void setButtonTextsIfNecessary() {
        String startTime = queryData.getAttribute(START_TIME);
        startTime = LectureQueryData.formatTime(startTime);
        btn_start_time.setText(startTime);

        String endTime = queryData.getAttribute(END_TIME);
        endTime = LectureQueryData.formatTime(endTime);
        btn_end_time.setText(endTime);

        String semesterText = queryData.getAttribute(SEMESTER);
        String semesterDisplayText = semesterText == null ? "Alle" : semesterText;
        btn_semester.setText(semesterDisplayText);

        String typText = queryData.getAttribute(TYP);
        String typDisplayText = semesterText == null ? "Alle" : typText;
        btn_typ.setText(typDisplayText);


        int weekDayAsInt = Integer.parseInt(queryData.getAttribute(WEEKDAY));
        String weekDay = wochentage[weekDayAsInt];
        btn_wochentag.setText(weekDay);

    }
}
