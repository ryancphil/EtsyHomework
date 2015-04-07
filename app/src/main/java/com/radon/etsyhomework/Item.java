package com.radon.etsyhomework;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The custom class is used to represent an individual item from ETSY.
 * The item's title, photo, price, shop, and description are stored.
 * Item implements Parcelable to they can be stored and reloaded from
 * a bundle on screen rotations instead of restarting the loader.
 */
public class Item implements Parcelable{

    String title;
    String url;
    String price;
    String shop;
    String description;

    Bitmap picture;

    Item(String title, String url, String price, String shop, String description){
        this.title = title;
        this.url = url;
        this.price = price;
        this.shop = shop;
        this.description = description;
    }

    public void setPicture(Bitmap picture){
        this.picture = picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //Used for savedInstanceState so items don't have to be reloaded from the API
    //on screen orientation changes.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(price);
        dest.writeString(shop);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){
        public Item createFromParcel(Parcel in){
            return new Item(in);
        }
        public Item[] newArray(int size){
            return new Item[size];
        }
    };

    private Item(Parcel in){
        title = in.readString();
        url = in.readString();
        price = in.readString();
        shop = in.readString();
        description = in.readString();
    }
}
