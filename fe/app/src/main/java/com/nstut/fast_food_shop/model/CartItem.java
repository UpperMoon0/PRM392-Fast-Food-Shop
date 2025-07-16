package com.nstut.fast_food_shop.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.nstut.fast_food_shop.data.models.ProductRoom;

public class CartItem implements Parcelable {
    private ProductRoom product;
    private int quantity;

    public CartItem() {
    }

    public CartItem(ProductRoom product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductRoom getProduct() {
        return product;
    }

    public void setProduct(ProductRoom product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        product = in.readParcelable(ProductRoom.class.getClassLoader());
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(product, flags);
        dest.writeInt(quantity);
    }
}