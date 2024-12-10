package edu.psu.ist.hcdd340.finalproject;

import androidx.appcompat.app.AppCompatActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;


public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "REGISTER_ACTIVITY";


    public static final String SHARED_PREF_NAME = "PENN_STATE_ID";
    public static final String TITLE_KEY = "TITLE";
    public static final String DESCRIPTION_KEY = "DESCRIPTION";
    public static final String DAY_KEY = "DAY";
    public static final String HOUR_KEY = "HOUR";
    public static final String TIMEOFDAY_KEY = "TIMEOFDAY";

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button createButton = findViewById(R.id.createEventButton);
        createButton.setOnClickListener(this);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        ArrayAdapter<CharSequence> time_adapter =
                ArrayAdapter.createFromResource(this, R.array.am_pm, android.R.layout.simple_spinner_item);

        time_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner;
        spinner = findViewById(R.id.amPmSpinner);
        spinner.setAdapter(time_adapter);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.backButton) {
            finish();
        } else if (id == R.id.createEventButton) {
            createEvent();
            Button button = findViewById(R.id.createEventButton);
            Snackbar.make(button,
                    "New Event created!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Returns user entered text from an EditText instance
     * @param id: Id of the EditText instance
     * @return User entered text
     */
    String getInputFromEditText(int id) {
        EditText v = findViewById(id);
        return v.getText().toString();
    }

    void createEvent(){
        String title = getInputFromEditText(R.id.titleInput);
        String description = getInputFromEditText(R.id.descriptionInput);
        String hour = getInputFromEditText(R.id.hourInput);
        String day = getInputFromEditText(R.id.dayInput);

        Spinner spinner = findViewById(R.id.amPmSpinner);
        String timeOfDay = (String) spinner.getSelectedItem();


        Log.d(TAG, "Title: " + title + ",  Description: " + description +
                ", Day: " + day + ", Hour: " + hour + timeOfDay);

        saveUserInformation(title, description, day, hour, timeOfDay);
    }

    void saveUserInformation(String title, String description, String day,
                             String hour, String timeOfDay) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TITLE_KEY, title);
        editor.putString(DESCRIPTION_KEY, description);
        editor.putString(DAY_KEY, day);
        editor.putString(HOUR_KEY, hour);

        editor.putString(TIMEOFDAY_KEY, timeOfDay);

        editor.apply();
    }
}
