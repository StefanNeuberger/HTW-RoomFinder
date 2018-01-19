package s53324_s53849.lsf_app.gui;

import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class MyTimePickerDialog extends TimePickerDialog {
    public MyTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener listener, Calendar calendar, String title) {
        super(context, THEME_HOLO_LIGHT, listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        setTitle(title);
    }
}
