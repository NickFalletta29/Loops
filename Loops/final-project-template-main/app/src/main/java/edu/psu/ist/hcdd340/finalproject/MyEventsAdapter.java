package edu.psu.ist.hcdd340.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyEventsAdapter extends RecyclerView.Adapter<MyEventsAdapter.ViewHolder> {

    private final ArrayList<MainActivity.Event> myEvents;

    public MyEventsAdapter(ArrayList<MainActivity.Event> myEvents) {
        this.myEvents = myEvents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.Event event = myEvents.get(position);
        holder.eventTitle.setText(event.getName());
        holder.eventDateTime.setText(event.getDate() + " - " + event.getTime());
        holder.eventImage.setImageResource(event.getImageResId());
        holder.eventDescription.setText(event.getDescription());
    }


    @Override
    public int getItemCount() {
        return myEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventDateTime, eventDescription;
        ImageView eventImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDateTime = itemView.findViewById(R.id.event_date_time);
            eventImage = itemView.findViewById(R.id.event_image);
            eventDescription = itemView.findViewById(R.id.event_description);
        }
    }

}
