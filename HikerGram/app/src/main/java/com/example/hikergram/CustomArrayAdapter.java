package com.example.hikergram;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<Post> {
    private final Context context;
    private final ArrayList<Post> values;

    public CustomArrayAdapter(Context context, int resource, ArrayList<Post> values) {
        super(context, resource, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.view_post, parent, false);

        // Get references to the views in the custom layout
        TextView textViewName = (TextView) rowView.findViewById(R.id.text_view_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.image_view);

        // Get the Post object for the current position
        Post post = values.get(position);

        // Populate the views with the data from the Post object
        textViewName.setText(post.getNameOfTheHike());

        Picasso.get().load(post.getImageUrl()).into(imageView);

        return rowView;
    }
}
