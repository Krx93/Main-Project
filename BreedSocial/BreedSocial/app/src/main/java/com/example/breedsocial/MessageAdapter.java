package com.example.breedsocial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        Message message = getItem(position);

        ImageView senderImageView = convertView.findViewById(R.id.avatar_image_view);
        TextView senderTextView = convertView.findViewById(R.id.sender_name_text_view);
        TextView messageTextView = convertView.findViewById(R.id.message_text_view);

        // Set sender image
        Glide.with(getContext())
                .load(message.getImage_url())
                .placeholder(R.drawable.ic_person_black)
                .into(senderImageView);

        // Set sender name
        senderTextView.setText(message.getName());

        // Set message content
        messageTextView.setText(message.getContent());

        LinearLayout messageLayout = convertView.findViewById(R.id.message_list_layout);


        return convertView;
    }
}
