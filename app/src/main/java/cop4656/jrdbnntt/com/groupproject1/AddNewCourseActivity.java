package cop4656.jrdbnntt.com.groupproject1;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Button;
import android.view.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Intent;
import android.widget.TimePicker;
import android.widget.Toast;

import cop4656.jrdbnntt.com.groupproject1.provider.MyContentProvider;
import cop4656.jrdbnntt.com.groupproject1.provider.table.Course;
import cop4656.jrdbnntt.com.groupproject1.provider.types.Time;
import cop4656.jrdbnntt.com.groupproject1.provider.types.WeekDayCollection;

public class AddNewCourseActivity extends AppCompatActivity {
    EditText etCourseName, etStartTime, etRoom;
    CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday;
    Button bSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_course);

        etCourseName = (EditText) findViewById(R.id.courseNum);
        etStartTime = (EditText) findViewById(R.id.startT);
        etRoom = (EditText) findViewById(R.id.roomNumT);
        cbMonday = (CheckBox) findViewById(R.id.mon);
        cbTuesday = (CheckBox) findViewById(R.id.tues);
        cbWednesday = (CheckBox) findViewById(R.id.wen);
        cbThursday = (CheckBox) findViewById(R.id.thur);
        cbFriday = (CheckBox) findViewById(R.id.fri);
        bSubmit = (Button) findViewById(R.id.submit);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitNewCourse()) {
                    startActivity(new Intent(getApplicationContext(), CoursesListActivity.class));
                }
            }
        });
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptForTime(etStartTime);
            }
        });
    }

    public boolean submitNewCourse() {
        Course course = new Course();

        // Construct course data from input
        course.name = etCourseName.getText().toString();
        course.room = etRoom.getText().toString();

        course.days = new WeekDayCollection();
        course.days.setDay(WeekDayCollection.WeekDay.MONDAY, cbMonday.isChecked());
        course.days.setDay(WeekDayCollection.WeekDay.TUESDAY, cbTuesday.isChecked());
        course.days.setDay(WeekDayCollection.WeekDay.WEDNESDAY, cbWednesday.isChecked());
        course.days.setDay(WeekDayCollection.WeekDay.THURSDAY, cbThursday.isChecked());
        course.days.setDay(WeekDayCollection.WeekDay.FRIDAY, cbFriday.isChecked());

        if (course.days.getEnabledDays().size() == 0) {
            Toast.makeText(this,"Invalid input: No days selected", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            course.startTime = new Time(etStartTime.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(this,"Invalid start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Save
        ContentValues values = new ContentValues();
        values.put(Course.COLUMN_NAME, course.name);
        values.put(Course.COLUMN_ROOM, course.room);
        values.put(Course.COLUMN_START_TIME, course.startTime.toString());
        values.put(Course.COLUMN_DAYS, course.days.toString());

        getContentResolver().insert(MyContentProvider.getUriForTable(Course.TABLE_NAME), values);

        return true;
    }


    public void promptForTime(final EditText editText) {
        TimePickerDialog.OnTimeSetListener setListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                editText.setText(dateFormat.format(calendar.getTime()));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, setListener, 8, 0, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.iViewSchedule:
                i = new Intent(this, CoursesListActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }
}