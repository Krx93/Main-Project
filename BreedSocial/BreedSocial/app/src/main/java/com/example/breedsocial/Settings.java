package com.example.breedsocial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Settings extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mSignedInAsTextView;
    private ImageView mProfileImageView;
    private FirebaseFirestore db;
    private int mCurrentImageViewId;
    private String mCurrentPhotoPath;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d("SettingsActivity", "onCreate() called");

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mSignedInAsTextView = findViewById(R.id.signed_in_as_textview);
        mProfileImageView = findViewById(R.id.profile_image);

        // Set click listener for profile image view
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SettingsActivity", "Profile image view clicked");
                mCurrentImageViewId = R.id.profile_image;
                // Launch image picker intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Get reference to the bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set the listener for navigation items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_button:
                        Intent homeIntent = new Intent(Settings.this, Home.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.profile_button:
                        Intent profileIntent = new Intent(Settings.this, Chat.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.settings_button:
                        // Do nothing, already on Settings activity
                        break;
                }
                return true;
            }
        });

        // Highlight the "Settings" item in the bottom navigation view
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        // Get a reference to the Update button
        Button updateButton = findViewById(R.id.update_button);

        // Set a click listener for the Update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the user's ID
                String userId = mAuth.getCurrentUser().getUid();

                // Get the updated name, age, and breed values
                EditText nameField = findViewById(R.id.name_field);
                String name = nameField.getText().toString();

                EditText ageField = findViewById(R.id.age_field);
                int age = Integer.parseInt(ageField.getText().toString());

                Spinner breedSpinner = findViewById(R.id.dropdown);
                String breed = breedSpinner.getSelectedItem().toString();

                // Check if the document exists for the user
                db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Document exists, update it with the new values
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("user_id", userId);
                                updates.put("name", name);
                                updates.put("age", age);
                                updates.put("breed", breed);
                                db.collection("users").document(userId)
                                        .update(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Document updated successfully
                                                Log.d("FirestoreSuccess", "Document updated successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to update document
                                                Log.e("FirestoreException", e.getMessage());
                                            }
                                        });
                            } else {
                                // Document does not exist, create a new document with default values
                                Map<String, Object> user = new HashMap<>();
                                user.put("user_id", userId);
                                user.put("name", name);
                                user.put("age", age);
                                user.put("breed", breed);
                                user.put("image_url", "");
                                db.collection("users").document(userId).set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Document created successfully
                                                Log.d("FirestoreSuccess", "Document created successfully");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to create document
                                                Log.e("FirestoreException", e.getMessage());
                                            }
                                        });
                            }
                        } else {
                            // Failed to get document
                            Log.e("FirestoreException", task.getException().getMessage());
                        }
                    }
                });
            }
        });



        Spinner dropdown = findViewById(R.id.dropdown);
        String[] items = new String[]{"Shih Tzu", "Pomeranian", "Pug", "Maltese", "Poodle"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        // Get the user's ID
        String userId = mAuth.getCurrentUser().getUid();

// Get a reference to the Firestore document for the user
        DocumentReference userRef = db.collection("users").document(userId);

// Get the user's information from Firestore and pre-fill the fields
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    int age = documentSnapshot.getLong("age").intValue();
                    String breed = documentSnapshot.getString("breed");
                    String imageUrl = documentSnapshot.getString("image_url");

                    // Set the values in the fields
                    EditText nameField = findViewById(R.id.name_field);
                    nameField.setText(name);

                    EditText ageField = findViewById(R.id.age_field);
                    ageField.setText(String.valueOf(age));

                    Spinner breedSpinner = findViewById(R.id.dropdown);
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) breedSpinner.getAdapter();
                    int position = adapter.getPosition(breed);
                    breedSpinner.setSelection(position);

                    // Load the user's image from Firebase Storage and display it in the ImageView
                    if (!imageUrl.equals("")) {
                        ImageView imageView = findViewById(R.id.profile_image);
                        Picasso.get().load(imageUrl).into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d("PicassoSuccess", "Image loaded successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e("PicassoError", e.getMessage());
                            }
                        });
                    }
                }
            }
        });

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String userId = mAuth.getCurrentUser().getUid();
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = findViewById(mCurrentImageViewId);
                imageView.setImageBitmap(bitmap);

                // Generate a unique name for the image
                String imageName = userId + "_" + System.currentTimeMillis() + ".jpg";

                // Upload the image to Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imageRef = storageRef.child("images/" + userId + "/" + imageName);
                UploadTask uploadTask = imageRef.putFile(uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.e("StorageException", exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful uploads
                        Log.d("StorageSuccess", "Image uploaded successfully");
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Add the image URL to Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> data = new HashMap<>();
                                data.put("url", uri.toString());
                                db.collection("users").document(userId)
                                        .collection("images").document(imageName)
                                        .set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Image URL added to Firestore successfully
                                                Log.d("FirestoreSuccess", "Image URL added to Firestore successfully");
                                                // Update the corresponding ImageView
                                                if (mCurrentImageViewId == R.id.profile_image) {
                                                    // If it's the profile image, load with Glide
                                                    Glide.with(Settings.this).load(uri).into(mProfileImageView);
                                                }
                                                // Update the document with the image URL
                                                db.collection("users").document(userId)
                                                        .update("image_url", uri.toString())
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                // Document updated successfully
                                                                Log.d("FirestoreSuccess", "Document updated successfully");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Failed to update document
                                                                Log.e("FirestoreException", e.getMessage());
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to add image URL to Firestore
                                                Log.e("FirestoreException", e.getMessage());
                                            }
                                        });
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    // ...
@Override
protected void onResume() {
    super.onResume();
    FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
        String email = user.getEmail();
        mSignedInAsTextView.setText("Signed in as: " + email);
    } else {
        // User is not signed in
    }

    // Load the user's latest image from Firebase Storage and display it in the ImageView

}



}

