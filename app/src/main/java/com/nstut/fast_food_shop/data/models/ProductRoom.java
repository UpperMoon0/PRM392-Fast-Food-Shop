package com.nstut.fast_food_shop.data.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "products")
public class ProductRoom implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int productId;
    public String name;
    public String description;
    public double price;
    public String imageUrl;
    public String categoryId;
    public boolean isAvailable;
    public String createdAt;
    public String updatedAt;

    @Ignore
    public ProductRoom(int productId, String name, String description, double price, String imageUrl, String categoryId, boolean isAvailable, String createdAt, String updatedAt) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.isAvailable = isAvailable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ProductRoom() {
    }

    public int getProductId() {
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    @Override
    public String toString() {
        return "ProductRoom{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", isAvailable=" + isAvailable +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
