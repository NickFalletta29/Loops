package edu.psu.ist.hcdd340.finalproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

    private static ArrayList<Event> myEventsList = new ArrayList<>();
    public static void addToMyEvents(Event event) {
        if (!myEventsList.contains(event)) {
            myEventsList.add(event);
        }
    }

    public static ArrayList<Event> getMyEventsList() {
        return myEventsList;
    }
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
                        int img = R.drawable.lion;

                        try {
                            img = Integer.parseInt(result.getData().getStringExtra("eventImage"));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing event image ID, using default.");
                        }

                        Event newEvent = new Event(title, date, time, description, 0, img);


                        eventList.add(newEvent);

                        String eventKey = JOINED_EVENTS_KEY + "_" + newEvent.getName() + "_" + newEvent.getDate();
                        sharedPreferences.edit().putBoolean(eventKey, true).apply();

                        addToMyEvents(newEvent);
                        updateUI();
                        Snackbar.make(findViewById(R.id.infoBtn), "Event created and joined successfully!", Snackbar.LENGTH_SHORT).show();
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

        loadFragment(new HomeFragment());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.nav_my_events:
                    selectedFragment = new MyEventsFragment();
                    break;
                // Other cases
            }
            return loadFragment(selectedFragment);
        });


    }


    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
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
        String eventKey = JOINED_EVENTS_KEY + "_" + currentEvent.getName() + "_" + currentEvent.getDate();
        boolean isJoined = sharedPreferences.getBoolean(eventKey, false);

        eventImage.setImageResource(currentEvent.getImageResId());
        eventInfo.setText(currentEvent.getName() + " - " + currentEvent.getDate() + (isJoined ? " (JOINED)" : ""));
    }


    public void addEvent(){

    }

    private void nextEvent() {
        if (currentIndex < eventList.size() - 1) {
            currentIndex++;
            updateUI();
            Log.d("MyEventsFragment", "myEventsList size: " + myEventsList.size());

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

        // Generate a unique key for the event based on its name and date
        String eventKey = JOINED_EVENTS_KEY + "_" + currentEvent.getName() + "_" + currentEvent.getDate();

        // Check if the event is already joined
        boolean isJoined = sharedPreferences.getBoolean(eventKey, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(currentEvent.getName() + (isJoined ? " - JOINED!" : ""))
                .setMessage("Date: " + currentEvent.getDate() +
                        "\nTime: " + currentEvent.getTime() +
                        "\n\nDescription: " + currentEvent.getDescription() +
                        "\n\nPeople Joined: " + currentEvent.getAttendees())
                .setNegativeButton(getString(R.string.cancel), null);

        if (isJoined) {
            // Option to leave the event
            builder.setPositiveButton(getString(R.string.leave), (dialog, which) -> {
                currentEvent.decreaseAttendees();

                // Remove the event from SharedPreferences
                sharedPreferences.edit().remove(eventKey).apply();

                // Remove the event from "My Events" list
                myEventsList.remove(currentEvent);

                Snackbar.make(findViewById(R.id.infoBtn), "You left the event.", Snackbar.LENGTH_SHORT).show();
                updateUI();
            });
        } else {
            // Option to join the event
            builder.setPositiveButton(getString(R.string.join), (dialog, which) -> {
                currentEvent.increaseAttendees();

                // Mark the event as joined in SharedPreferences
                sharedPreferences.edit().putBoolean(eventKey, true).apply();

                // Add the event to "My Events" list
                addToMyEvents(currentEvent);

                Snackbar.make(findViewById(R.id.infoBtn), "You joined the event!", Snackbar.LENGTH_SHORT).show();
                updateUI();
            });
        }

        builder.show();
    }


    private void saveEventToSharedPrefs(Event event) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String eventJson = String.format("{\"name\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"description\":\"%s\",\"attendees\":%d,\"imageResId\":%d}",
                event.getName(), event.getDate(), event.getTime(), event.getDescription(), event.getAttendees(), event.getImageResId());
        editor.putString(event.getName() + "_" + event.getDate(), eventJson);
        editor.apply();
    }

    private void removeEventFromSharedPrefs(Event event) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(event.getName() + "_" + event.getDate());
        editor.apply();
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

    public static class Event {
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

