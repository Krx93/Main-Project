package com.example.breedsocial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.breedsocial.Message;

public class ChatActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private String userId;
    private User otherUser;
    private ListView messageListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageListView = findViewById(R.id.message_list_view);
        final User otherUser = getIntent().getParcelableExtra("otherUser");

        db = FirebaseFirestore.getInstance();
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (otherUser != null) {
            Log.d("ChatActivity", "Other user ID: " + otherUser.getUser_id());
            userId = otherUser.getUser_id();
        } else {
            Log.e("ChatActivity", "otherUser is null");
        }

        Log.d("ChatActivity", "Current user ID: " + currentUser);
        Log.d("ChatActivity", "Other user ID: " + otherUser);



        // Create a conversation document for each user
        String currentUserId = currentUser.getUid();
        String otherUserId = otherUser.getUser_id();
        String[] userIds = new String[]{currentUserId, otherUserId};
        Arrays.sort(userIds);
        String currentConversationId = userIds[0] + "_" + userIds[1];

        // Create a conversation object
        Map<String, Object> conversation = new HashMap<>();
        conversation.put("user1", currentUserId);
        conversation.put("user2", otherUserId);

        // Add the conversation object to the current user's "conversations" subcollection
        db.collection("users").document(currentUserId).collection("conversations").document(currentConversationId)
                .set(conversation)
                .addOnSuccessListener(aVoid -> {
                    // Conversation document created successfully or updated successfully
                    Log.d("ChatActivity", "Conversation document created or updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Error creating or updating conversation document
                    Log.e("ChatActivity", "Error creating or updating conversation document", e);
                });

// Retrieve the current user's name and image URL from the Firestore users collection
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String image_url = documentSnapshot.getString("image_url");
                        Log.d("ChatActivity", "Current user's name: " + name);
                        Log.d("ChatActivity", "Current user's image URL: " + image_url);
                        // Send button implementation
                        EditText messageInput = findViewById(R.id.message_input);
                        Button sendButton = findViewById(R.id.send_button);
                        sendButton.setOnClickListener(view -> {
                            String message = messageInput.getText().toString().trim();
                            if (!message.isEmpty()) {
                                // Create a new message document
                                Map<String, Object> newMessage = new HashMap<>();
                                newMessage.put("sender_id", currentUser.getUid());
                                newMessage.put("other_id", otherUser.getUser_id());
                                newMessage.put("content", message);
                                newMessage.put("timestamp", FieldValue.serverTimestamp());
                                newMessage.put("name", name); // Add sender's name
                                newMessage.put("image_url", image_url); // Add sender's image URL
                                // Add the message document to the conversation
                                db.collection("conversations").document(currentConversationId)
                                        .collection("messages").add(newMessage)
                                        .addOnSuccessListener(documentReference -> {
                                            // Message document created successfully
                                            messageInput.setText(""); // Clear the message input
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error creating message document
                                            Log.e("ChatActivity", "Error creating message document", e);
                                        });
                            }
                        });
                    } else {
                        Log.e("ChatActivity", "Current user document does not exist in Firestore");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ChatActivity", "Error retrieving current user document from Firestore", e);
                });

// Send button implementation
        EditText messageInput = findViewById(R.id.message_input);
        Button sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                // Create a new message document
                Map<String, Object> newMessage = new HashMap<>();
                newMessage.put("sender_id", currentUser.getUid());
                newMessage.put("content", message);
                newMessage.put("timestamp", FieldValue.serverTimestamp());
                // Add the message document to the conversation
                db.collection("conversations").document(currentConversationId)
                        .collection("messages").add(newMessage)
                        .addOnSuccessListener(documentReference -> {
                            // Message document created successfully
                            messageInput.setText(""); // Clear the message input
                        })
                        .addOnFailureListener(e -> {
                            // Error creating message document
                            Log.e("ChatActivity", "Error creating message document", e);
                        });
            }
        });




        // Retrieve chat history
        db.collection("conversations").document(currentConversationId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.e("ChatActivity", "Error retrieving chat history", error);
                        return;
                    }
                    List<Message> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        Message message = documentSnapshot.toObject(Message.class);
                        messages.add(message);
                    }

                    MessageAdapter adapter = new MessageAdapter(ChatActivity.this, messages);
                    messageListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_button:
                    startActivity(new Intent(ChatActivity.this, Home.class));
                    return true;
                case R.id.profile_button:
                    // Handle profile button click
                    return true;
                case R.id.settings_button:
                    startActivity(new Intent(ChatActivity.this, Settings.class));
                    return true;
                default:
                    return false;
            }
        });

        // Highlight the "profile" button in the bottom navigation bar
        bottomNavigationView.getMenu().findItem(R.id.profile_button).setChecked(true);
    }
}
