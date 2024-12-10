package edu.psu.ist.hcdd340.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Event> eventList;
    private int currentIndex = 0;
    private ImageView eventImage;
    private TextView eventInfo;
    private Button infoBtn;
    private SharedPreferences sharedPreferences;
    private static final String JOINED_EVENTS_KEY = "joined_events";

    private ActivityResultLauncher<Intent> createEventLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE);
        createEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String title = result.getData().getStringExtra("eventTitle");
                        String description = result.getData().getStringExtra("eventDescription");
                        String date = result.getData().getStringExtra("eventDate");
                        String time = result.getData().getStringExtra("eventTime");
                        int img = 0;

                        try {
                            img = Integer.parseInt(result.getData().getStringExtra("eventImage"));
                        } catch (NumberFormatException e) {
                            eventList.add(new Event(title, date, time, description, 0, R.drawable.lion));
                            updateUI();
                        }

                        eventList.add(new Event(title, date, time, description, 0, R.drawable.lion));
                        updateUI();
                    }
                }
        );


        eventImage = findViewById(R.id.eventImage);
        eventInfo = findViewById(R.id.eventInfo);
        infoBtn = findViewById(R.id.infoBtn);

        currentEvents();
        sortPopular();
        updateUI();

        infoBtn.setOnClickListener(view -> showEventInfo());

        eventImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float x = event.getX();
                    float width = v.getWidth();
                    if (x < width / 2) {
                        prevEvent();
                    } else {
                        nextEvent();
                    }
                }
                return true;
            }
        });
    }

    private void currentEvents() {
        eventList = new ArrayList<>();
        eventList.add(new Event("Old Main Hangout", "12/5/24", "3:00 PM", "Looking for people to hang out with at Old Main!", 7, R.drawable.old_main));
        eventList.add(new Event("IM Soccer", "12/6/24", "7:00 PM", "Need a bunch of people for a soccer tournament", 8, R.drawable.soccer));
        eventList.add(new Event("Football Game", "11/9/24", "8:00 PM", "Anyone want to join me for the White Out?", 9, R.drawable.game));
        eventList.add(new Event("Dinner at South", "12/4/24", "5:30 PM", "Anyone want to grab dinner with me?", 10, R.drawable.dining));
    }

    private void updateUI() {
        Event currentEvent = eventList.get(currentIndex);
        eventImage.setImageResource(currentEvent.getImageResId());
        eventInfo.setText(currentEvent.getName() + " - " + currentEvent.getDate());

    }

    public void addEvent(){

    }

    private void nextEvent() {
        if (currentIndex < eventList.size() - 1) {
            currentIndex++;
            updateUI();
        }
    }

    private void prevEvent() {
        if (currentIndex > 0) {
            currentIndex--;
            updateUI();
        }
    }

    private void showEventInfo() {
        Event currentEvent = eventList.get(currentIndex);

        boolean isJoined = sharedPreferences.getBoolean(JOINED_EVENTS_KEY + currentIndex, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentEvent.getName() + (isJoined ? " - JOINED!" : ""))
                .setMessage("Date: " + currentEvent.getDate() +
                        "\nTime: " + currentEvent.getTime() +
                        "\n\nDescription: " + currentEvent.getDescription() +
                        "\n\nPeople Joined: " + currentEvent.getAttendees())
                .setNegativeButton(getString(R.string.cancel), null);

        if (isJoined) {
            builder.setPositiveButton(getString(R.string.leave), (dialog, which) -> {
                currentEvent.decreaseAttendees();
                sharedPreferences.edit().remove(JOINED_EVENTS_KEY + currentIndex).apply();
                Snackbar.make(findViewById(R.id.infoBtn), "You left the event.", Snackbar.LENGTH_SHORT).show();
                updateUI();
            });
        } else {
            builder.setPositiveButton(getString(R.string.join), (dialog, which) -> {
                currentEvent.increaseAttendees();
                sharedPreferences.edit().putBoolean(JOINED_EVENTS_KEY + currentIndex, true).apply();
                Snackbar.make(findViewById(R.id.infoBtn), "You joined the event!", Snackbar.LENGTH_SHORT).show();
                updateUI();
            });
        }

        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popular:
                sortPopular();
                return true;
            case R.id.recent:
                sortDate();
                return true;
            case R.id.createEvent:
                // Handle the create event action
                Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
                createEventLauncher.launch(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortPopular() {
        Collections.sort(eventList, (e1, e2) -> Integer.compare(e2.getAttendees(), e1.getAttendees()));
        currentIndex = 0;
        updateUI();
    }

    private void sortDate() {
        Collections.sort(eventList, (e1, e2) -> e1.getDate().compareTo(e2.getDate()));
        currentIndex = 0;
        updateUI();
    }

    public class Event {
        private String name, date, time, description;
        private int attendees;
        private int imageResId;

        public Event(String name, String date, String time, String description, int attendees, int imageResId) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.description = description;
            this.attendees = attendees;
            this.imageResId = imageResId;
        }



        public String getName() {
            return name;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }

        public int getAttendees() {
            return attendees;
        }

        public int getImageResId() {
            return imageResId;
        }

        public void increaseAttendees() {
            attendees++;
        }

        public void decreaseAttendees() {
            if (attendees > 0) attendees--;
        }
    }
}

