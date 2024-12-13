package edu.psu.ist.hcdd340.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import edu.psu.ist.hcdd340.finalproject.MainActivity;

public class MyEventsFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyEventsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_events, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_my_events);
        TextView emptyMessage = view.findViewById(R.id.empty_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ArrayList<MainActivity.Event> myEventsList = MainActivity.getMyEventsList();

        if (myEventsList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            MyEventsAdapter adapter = new MyEventsAdapter(myEventsList);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }

}
