package com.example.breedsocial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<Conversation> conversationList;
    private ConversationAdapter adapter;

    private String getOtherUserIdFromConversation(Conversation conversation, String currentUserId) {
        for (String userId : conversation.getUsers().keySet()) {
            if (!userId.equals(currentUserId)) {
                return userId;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        Log.d("ConversationListActivity", "Starting conversation list activity...");

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        conversationList = new ArrayList<>();
        adapter = new ConversationAdapter(conversationList, position -> {
            // Handle click events on conversation items here
        });

        RecyclerView recyclerView = findViewById(R.id.conversation_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        CollectionReference conversationsRef = db.collection("conversations");
        conversationsRef
                .whereEqualTo("users." + currentUser.getUid(), true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ConversationListActivity", "Retrieved conversations from database");
                        for (QueryDocumentSnapshot conversationDoc : task.getResult()) {
                            Conversation conversation = conversationDoc.toObject(Conversation.class);
                            conversation.setConversation_id(conversationDoc.getId());

                            // Get the other user's ID from the conversation document
                            String otherUserId;
                            if (conversation.getSender_id().equals(currentUser.getUid())) {
                                otherUserId = conversation.getOther_id();
                                Log.d("ConversationListActivity", "User ID for conversation: " + conversation.getConversation_id() + " - Sender ID: " + conversation.getSender_id() + ", Other ID: " + conversation.getOther_id() + ", Current User ID: " + currentUser.getUid() + " - Other user ID is " + otherUserId);
                            } else {
                                otherUserId = conversation.getSender_id();
                                Log.d("ConversationListActivity", "User ID for conversation: " + conversation.getConversation_id() + " - Sender ID: " + conversation.getSender_id() + ", Other ID: " + conversation.getOther_id() + ", Current User ID: " + currentUser.getUid() + " - Other user ID is " + otherUserId);
                            }


                            // Get the last message in the conversation
                            db.collection("conversations")
                                    .document(conversation.getConversation_id())
                                    .collection("messages")
                                    .orderBy("timestamp", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnCompleteListener(messageTask -> {
                                        if (messageTask.isSuccessful()) {
                                            Log.d("ConversationListActivity", "Retrieved messages for conversation: " + conversation.getConversation_id());
                                            for (QueryDocumentSnapshot messageDoc : messageTask.getResult()) {
                                                Message message = messageDoc.toObject(Message.class);
                                                conversation.setContent(message.getContent());
                                                Log.d("ConversationListActivity", "Retrieved message for conversation: " + message.getContent());
                                            }
                                        } else {
                                            Log.e("ConversationListActivity", "Error retrieving last message for conversation: " + conversation.getConversation_id(), messageTask.getException());
                                        }

                                        // Get the other user's document from the users collection
                                        DocumentReference otherUserDocRef = db.collection("users").document(otherUserId);
                                        otherUserDocRef.get()
                                                .addOnCompleteListener(userTask -> {
                                                    if (userTask.isSuccessful()) {
                                                        DocumentSnapshot otherUserDoc = userTask.getResult();
                                                        if (otherUserDoc.exists()) {
                                                            String name = otherUserDoc.getString("name");
                                                            String image_url = otherUserDoc.getString("image_url");
                                                            Log.d("ConversationListActivity", "Retrieved user document for conversation: name=" + name + ", imageUrl=" + image_url);
                                                            Log.d("ConversationListActivity", "Number of conversations retrieved: " + task.getResult().size());

                                                            conversation.setName(name);
                                                            conversation.setImage_url(image_url);

                                                            // Add the conversation to the list
                                                            conversationList.add(conversation);
                                                            Log.d("ConversationListActivity", "Number of conversations retrieved: " + task.getResult().size());

                                                            // Notify the adapter of the data change
                                                            adapter.notifyItemInserted(conversationList.size());


                                                        } else {
                                                            Log.e("ConversationListActivity", "Error: user document does not exist for user with ID " + otherUserId);
                                                        }
                                                    } else {
                                                        Log.e("ConversationListActivity", "Error retrieving user document for user with ID " + otherUserId, userTask.getException());
                                                    }

                                                });

                                    });
                        }
                    } else {
                        Log.e("ConversationListActivity", "Error retrieving conversations: ", task.getException());
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_button:
                    startActivity(new Intent(ConversationListActivity.this, Home.class));
                    return true;
                case R.id.profile_button:

                    return true;
                case R.id.settings_button:
                    startActivity(new Intent(ConversationListActivity.this, Settings.class));
                    return true;
                default:
                    return false;
            }
        });

        // Highlight the "home" button in the bottom navigation bar
        bottomNavigationView.getMenu().findItem(R.id.home_button).setChecked(true);
    }
}
