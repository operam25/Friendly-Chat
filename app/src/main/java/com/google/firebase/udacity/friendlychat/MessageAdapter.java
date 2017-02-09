package com.google.firebase.udacity.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<FriendlyMessageReceived> {
    public MessageAdapter(Context context, int resource, List<FriendlyMessageReceived> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FriendlyMessageReceived message = getItem(position);

        if (!message.getName().isEmpty()) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }else {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_send_message, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }
        Date mDate = new Date(message.getTime());
        SimpleDateFormat sdfDate = new SimpleDateFormat("hh:mm:ss a d/M/yy");
        String strDate = sdfDate.format(mDate);
        timeTextView.setText(strDate);
        return convertView;
    }
}
