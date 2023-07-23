package com.example.breedsocial;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private int age;
    private String breed;
    private String image_url;
    private String user_id;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String name, int age, String breed, String image_url, String user_id) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.image_url = image_url;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUser_id() {
        return user_id != null ? user_id : "";
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    // Implement the Parcelable interface's describeContents method
    @Override
    public int describeContents() {
        return 0;
    }

    // Implement the Parcelable interface's writeToParcel method
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.age);
        dest.writeString(this.breed);
        dest.writeString(this.image_url);
        dest.writeString(this.user_id);
    }

    // Implement the Parcelable interface's CREATOR field
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Implement a private constructor that reads from a Parcel
    private User(Parcel in) {
        this.name = in.readString();
        this.age = in.readInt();
        this.breed = in.readString();
        this.image_url = in.readString();
        this.user_id = in.readString();
    }
}
