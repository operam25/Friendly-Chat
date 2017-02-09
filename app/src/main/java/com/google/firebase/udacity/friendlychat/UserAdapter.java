package com.google.firebase.udacity.friendlychat;

/**
 * Created by khandelwal on 09/02/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends ArrayAdapter<UserList> {
    public UserAdapter(Context context, int resource, List<UserList> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_user, parent, false);
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.userNameTextView);

        UserList message = getItem(position);

        if(message.getName()!=null)
            messageTextView.setText(message.getName());

        return convertView;
    }
}
