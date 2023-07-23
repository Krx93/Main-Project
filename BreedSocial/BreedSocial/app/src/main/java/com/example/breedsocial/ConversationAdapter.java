package com.example.breedsocial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<Conversation> mConversationList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ConversationAdapter(List<Conversation> conversationList, OnItemClickListener listener) {
        mConversationList = conversationList;
        mListener = listener;

        // Add some hardcoded conversations for testing
        mConversationList.add(new Conversation("John", "Hi there!"));
        mConversationList.add(new Conversation("Jane", "Hey! How are you?"));
        mConversationList.add(new Conversation("Mark", "What's up?"));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameTextView;
        public TextView mLastMessageTextView;
        public ImageView mProfileImageView;

        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            mNameTextView = itemView.findViewById(R.id.sender_name_text_view);
            mLastMessageTextView = itemView.findViewById(R.id.message_text_view);
            mProfileImageView = itemView.findViewById(R.id.avatar_image_view);


            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation conversation = mConversationList.get(position);
        holder.mNameTextView.setText(conversation.getName());
        holder.mLastMessageTextView.setText(conversation.getText());
        Glide.with(holder.itemView.getContext())
                .load(conversation.getImage_url())
                .placeholder(R.drawable.ic_person_black)
                .into(holder.mProfileImageView);
    }

    @Override
    public int getItemCount() {
        return mConversationList.size();
    }
}
