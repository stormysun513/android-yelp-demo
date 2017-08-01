package edu.cmu.demoapp2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yu-Lun Tsai on 31/07/2017.
 */

public class RestaurantInfoListAdapter extends ArrayAdapter<RestaurantInfoCell> {

    public RestaurantInfoListAdapter(Context context, List<RestaurantInfoCell> restaurants) {
        super(context, 0, restaurants);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        RestaurantInfoCell cell = getItem(position);
        ImageViewHolder viewHolder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(
                    getContext()).inflate(R.layout.listview_cell, parent, false);
            viewHolder = new ImageViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.restaurantImage);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ImageViewHolder)convertView.getTag();
        if(viewHolder.bitmap == null){
            // get corresponding profile URL asynchronously
            viewHolder.imageUrl = cell.getImageUrl();
            new GetImageInViewHolderTask().execute(viewHolder);
        }
        else{
            viewHolder.imageView.setImageBitmap(viewHolder.bitmap);
        }

        // Lookup view for data population
        TextView nameLabel = convertView.findViewById(R.id.nameLabel);
        TextView descLabel = convertView.findViewById(R.id.descLabel);

        // Populate the data into the template view using the data object
        nameLabel.setText(cell.getName());
        descLabel.setText(cell.getDescription());

        // Return the completed view to render on screen
        return convertView;
    }
}