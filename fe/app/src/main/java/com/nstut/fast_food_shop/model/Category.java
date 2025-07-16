package com.nstut.fast_food_shop.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Category implements Serializable, Parcelable {
    private int id;
    private String name;
    private String description;
    private String imageUrl;

    public Category(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);
    }
}