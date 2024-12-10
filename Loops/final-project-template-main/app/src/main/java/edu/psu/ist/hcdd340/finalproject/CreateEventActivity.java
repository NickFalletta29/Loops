package edu.psu.ist.hcdd340.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;


public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateEventActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView eventImage;
    private EditText titleInput;
    private ImageView refreshBtn;
    private EditText descriptionInput;
    private Spinner monthSpinner, daySpinner, hourSpinner, minuteSpinner, amPmSpinner;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventImage = findViewById(R.id.eventImage);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        monthSpinner = findViewById(R.id.monthSpinner);
        daySpinner = findViewById(R.id.daySpinner);
        hourSpinner = findViewById(R.id.hourSpinner);
        minuteSpinner = findViewById(R.id.minuteSpinner);
        amPmSpinner = findViewById(R.id.amPmSpinner);

        // Set adapters for spinners
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(hourAdapter);

        ArrayAdapter<CharSequence> minuteAdapter = ArrayAdapter.createFromResource(this, R.array.minutes, android.R.layout.simple_spinner_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteSpinner.setAdapter(minuteAdapter);

        ArrayAdapter<CharSequence> amPmAdapter = ArrayAdapter.createFromResource(this, R.array.am_pm, android.R.layout.simple_spinner_item);
        amPmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amPmSpinner.setAdapter(amPmAdapter);

        Button createButton = findViewById(R.id.createEventButton);
        createButton.setOnClickListener(this);
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
        ImageView refreshbtn = findViewById(R.id.infoButton);
        refreshbtn.setOnClickListener(view -> {
            clearInputs();
        });
        eventImage.setOnClickListener(v -> openImagePicker());
        sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE);
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

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            eventImage.setImageURI(data.getData());
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

        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String month = monthSpinner.getSelectedItem().toString();
        String day = daySpinner.getSelectedItem().toString();
        String hour = hourSpinner.getSelectedItem().toString();
        String minute = minuteSpinner.getSelectedItem().toString();
        String amPm = amPmSpinner.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Snackbar.make(findViewById(R.id.createEventButton), "All fields must be filled!", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Event Created: " + title + ", " + description + ", Date: " + month + " " + day + ", Time: " + hour + ":" + minute + " " + amPm);

        // Send back to MainActivity
        Intent intent = new Intent();
        intent.putExtra("eventTitle", title);
        intent.putExtra("eventDescription", description);
        intent.putExtra("eventDate", month + " " + day);
        intent.putExtra("eventTime", hour + ":" + minute + " " + amPm);
        setResult(RESULT_OK, intent);

        clearInputs();


//        String title = getInputFromEditText(R.id.titleInput);
//        String description = getInputFromEditText(R.id.descriptionInput);
//        String hour = getInputFromEditText(R.id.hourInput);
//        String day = getInputFromEditText(R.id.dayInput);
//
//        Spinner spinner = findViewById(R.id.amPmSpinner);
//        String timeOfDay = (String) spinner.getSelectedItem();
//
//
//        Log.d(TAG, "Title: " + title + ",  Description: " + description +
//                ", Day: " + day + ", Hour: " + hour + timeOfDay);
//
//        saveUserInformation(title, description, day, hour, timeOfDay);
    }
    private void clearInputs() {
        titleInput.setText("");
        descriptionInput.setText("");
        monthSpinner.setSelection(0);
        daySpinner.setSelection(0);
        hourSpinner.setSelection(0);
        minuteSpinner.setSelection(0);
        amPmSpinner.setSelection(0);
        eventImage.setImageResource(R.drawable.ic_image_placeholder);
    }

//    void saveUserInformation(String title, String description, String day,
//                             String hour, String timeOfDay) {
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(TITLE_KEY, title);
//        editor.putString(DESCRIPTION_KEY, description);
//        editor.putString(DAY_KEY, day);
//        editor.putString(HOUR_KEY, hour);
//
//        editor.putString(TIMEOFDAY_KEY, timeOfDay);
//
//        editor.apply();
//    }
}
