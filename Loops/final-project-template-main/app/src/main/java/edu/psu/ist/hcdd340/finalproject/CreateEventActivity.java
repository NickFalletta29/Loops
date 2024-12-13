package edu.psu.ist.hcdd340.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateEventActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView eventImage;
    private EditText titleInput;
    private EditText descriptionInput;
    private EditText dateInput;
    private Spinner hourSpinner, minuteSpinner, amPmSpinner;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventImage = findViewById(R.id.eventImage);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dateInput = findViewById(R.id.dateInput);

        hourSpinner = findViewById(R.id.hourSpinner);
        minuteSpinner = findViewById(R.id.minuteSpinner);
        amPmSpinner = findViewById(R.id.amPmSpinner);


        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        hourSpinner.setAdapter(hourAdapter);

        ArrayAdapter<CharSequence> minuteAdapter = ArrayAdapter.createFromResource(this, R.array.minutes, android.R.layout.simple_spinner_item);
        minuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        minuteSpinner.setAdapter(minuteAdapter);

        ArrayAdapter<CharSequence> amPmAdapter = ArrayAdapter.createFromResource(this, R.array.am_pm, android.R.layout.simple_spinner_item);
        amPmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        amPmSpinner.setAdapter(amPmAdapter);


        dateInput.setOnClickListener(v -> showDatePickerDialog());


        Button createButton = findViewById(R.id.createEventButton);
        createButton.setOnClickListener(this);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        ImageView refreshButton = findViewById(R.id.infoButton);
        refreshButton.setOnClickListener(view -> clearInputs());

        
        eventImage.setOnClickListener(v -> openImagePicker());

        sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.createEventButton) {
            createEvent();
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_DeviceDefault_Dialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    dateInput.setText(date);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    void createEvent() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        String date = dateInput.getText().toString();
        String hour = hourSpinner.getSelectedItem().toString();
        String minute = minuteSpinner.getSelectedItem().toString();
        String amPm = amPmSpinner.getSelectedItem().toString();
        int img = 0;

        String time = hour + ":" + minute + " " + amPm;

        MainActivity.Event newEvent = new MainActivity.Event(title, date, time, description, 0, R.drawable.lion);

        // Send back to MainActivity
        Intent intent = new Intent();
        intent.putExtra("eventTitle", titleInput.getText().toString());
        intent.putExtra("eventDescription", descriptionInput.getText().toString());
        intent.putExtra("eventDate", dateInput.getText().toString());
        intent.putExtra("eventTime", time);
        intent.putExtra("eventImage", img);

        SharedPreferences sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(title + "_" + date, String.format("{\"name\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"description\":\"%s\",\"attendees\":0,\"imageResId\":%d}",
                title, date, time, description, R.drawable.lion));
        editor.apply();

        setResult(RESULT_OK, intent);
        finish();

        Snackbar.make(findViewById(R.id.createEventButton), "New Event created!", Snackbar.LENGTH_LONG).show();
    }

    private void clearInputs() {
        titleInput.setText("");
        descriptionInput.setText("");
        dateInput.setText("");
        hourSpinner.setSelection(0);
        minuteSpinner.setSelection(0);
        amPmSpinner.setSelection(0);
        eventImage.setImageResource(R.drawable.ic_image_placeholder);
    }
}

