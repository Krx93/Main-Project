package com.example.breedsocial;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.breedsocial.LandingActivity;
import com.example.breedsocial.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private User randomUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_button:
                        // Handle home button click
                        return true;
                    case R.id.profile_button:
                        startActivity(new Intent(Home.this, ConversationListActivity.class));
                        return true;
                    case R.id.settings_button:
                        startActivity(new Intent(Home.this, Settings.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        ImageButton button2 = findViewById(R.id.button2);



        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reload the activity to display a new random user
                finish();
                startActivity(getIntent());
            }
        });


        // Highlight the home button as the current page
        bottomNavigationView.getMenu().findItem(R.id.home_button).setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not signed in, redirect to Login Activity
            startActivity(new Intent(Home.this, LandingActivity.class));
            finish();
        } else {
            // User is signed in, update UI accordingly.
            String currentUserId = currentUser.getUid();
            Log.d("Home", "Current user id: " + currentUserId);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            DocumentReference currentUserRef = usersRef.document(currentUserId);
            currentUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String currentBreed = documentSnapshot.getString("breed");
                    Log.d("Home", "Current user breed: " + currentBreed);
                    // Fetch all users from Firestore and filter them based on their breed
                    usersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            List<User> users = new ArrayList<>();
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                User user = document.toObject(User.class);
                                if (!user.getUser_id().equals(currentUserId)) {
                                    users.add(user);
                                }
                            }

                            Log.d("Home", "Number of users fetched: " + users.size());

                            // Shuffle the list of users with a weighted distribution
                            List<User> shuffledUsers = new ArrayList<>();
                            for (User user : users) {
                                if (user.getBreed().equals(currentBreed)) {
                                    // Add the user to the shuffled list 3 times (higher weight)
                                    shuffledUsers.add(user);
                                    shuffledUsers.add(user);
                                    shuffledUsers.add(user);
                                } else {
                                    // Add the user to the shuffled list once (lower weight)
                                    shuffledUsers.add(user);
                                }
                                Log.d("Home", "User ID added to shuffled list: " + user.getUser_id());
                            }


                            Log.d("Home", "Shuffled users: " + shuffledUsers);

                            Log.d("Home", "Number of users after weighting: " + shuffledUsers.size());

                            Collections.shuffle(shuffledUsers);

                            if (!shuffledUsers.isEmpty()) {
                                // Get the first user from the shuffled list
                                randomUser = shuffledUsers.get(0);
                                Log.d("Home", "Random user id: " + randomUser.getUser_id());
                                Log.d("Home", "Random user object: " + randomUser);
                                // Load user image into CardView using Glide
                                CardView cardView = findViewById(R.id.card_view);
                                ImageView imageView = cardView.findViewById(R.id.card_image);
                                Glide.with(getApplicationContext())
                                        .load(randomUser.getImage_url())
                                        .into(imageView);

                                // Set user's name, age, and breed into their corresponding TextViews
                                TextView nameTextView = findViewById(R.id.text_name);
                                nameTextView.setText(randomUser.getName());

                                TextView ageTextView = findViewById(R.id.text_age);
                                ageTextView.setText(String.valueOf(randomUser.getAge()));

                                TextView breedTextView = findViewById(R.id.text_breed);
                                breedTextView.setText(randomUser.getBreed());
                                Log.d("Home", "Number of users after weighting: " + shuffledUsers.size());



                                // Set click listener for button1
                                ImageButton button1 = findViewById(R.id.button1);
                                button1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (randomUser != null) {
                                            Intent intent = new Intent(Home.this, ChatActivity.class);
                                            intent.putExtra("currentUserId", currentUserId);
                                            intent.putExtra("otherUser", randomUser);
                                            startActivity(intent);
                                            Log.d("Home", "Current user id: " + currentUserId + ", Other user id: " + randomUser.getUser_id());
                                        }
                                    }
                                });

                            } else {
                                // Hide the card view if there are no other users to display
                                CardView cardView = findViewById(R.id.card_view);
                                cardView.setVisibility(View.GONE);
                            }

                        }
                    });
                }
            });
        }
    }
}

