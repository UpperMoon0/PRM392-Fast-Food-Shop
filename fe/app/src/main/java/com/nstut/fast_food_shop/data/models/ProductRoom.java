package com.nstut.fast_food_shop.data.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "products")
public class ProductRoom implements Serializable, Parcelable {
    @PrimaryKey(autoGenerate = true)
    public int productId;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public boolean isAvailable;
    public String createdAt;
    public String updatedAt;
    public List<String> categoryIds;
 
    @Ignore
    public ProductRoom(int productId, String name, String description, double price, String imageUrl, boolean isAvailable, String createdAt, String updatedAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductRoom() {
    }

    protected ProductRoom(Parcel in) {
        productId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        isAvailable = in.readByte() != 0;
        createdAt = in.readString();
        updatedAt = in.readString();
        categoryIds = in.createStringArrayList();
    }

    public static final Creator<ProductRoom> CREATOR = new Creator<ProductRoom>() {
        @Override
        public ProductRoom createFromParcel(Parcel in) {
            return new ProductRoom(in);
        }

        @Override
        public ProductRoom[] newArray(int size) {
            return new ProductRoom[size];
        }
    };

    public int getId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeByte((byte) (isAvailable ? 1 : 0));
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeStringList(categoryIds);
    }
  
    @Override
    public String toString() {
        return "ProductRoom{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", isAvailable=" + isAvailable +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", categoryIds=" + categoryIds +
                '}';
    }
}
