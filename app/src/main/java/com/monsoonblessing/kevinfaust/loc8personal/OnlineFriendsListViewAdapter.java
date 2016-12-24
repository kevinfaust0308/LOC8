package com.monsoonblessing.kevinfaust.loc8personal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2016-12-23.
 */

public class OnlineFriendsListViewAdapter extends ArrayAdapter<Marker> {


    public OnlineFriendsListViewAdapter(Context context, List<Marker> objects) {
        super(context, R.layout.online_friends_list_view_row, objects);
    }


    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView status;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Marker marker = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.online_friends_list_view_row, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.online_friend_name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.online_friend_status);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.name.setText(marker.getTitle());
        viewHolder.status.setText(marker.getSnippet());

        // Return the completed view to render on screen
        return convertView;
    }

}
