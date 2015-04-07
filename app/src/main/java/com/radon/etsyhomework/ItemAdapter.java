package com.radon.etsyhomework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Custom adapter for populating a listview with Item objects.
 */
public class ItemAdapter extends ArrayAdapter<Item> {

    public ItemAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        //Take advantage of the wonderful ViewHolder Design Pattern
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.item_view, null);
            holder = new ViewHolder();
            holder.photo = (ImageView) view.findViewById(R.id.item_picture);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.shop = (TextView) view.findViewById(R.id.shop);
            holder.price = (TextView) view.findViewById(R.id.price);
            view.setTag(holder);
        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.photo.setImageResource(R.drawable.place_holder);
        //Get the item at the position in the list
        Item item = getItem(position);


        if(item != null){
            //Use an asyncTask to load pictures separately.
            if(item.picture == null){
                new LoadPictures(view,holder.photo).execute(item);
            }else{
                holder.photo.setImageBitmap(item.picture);
            }

            if(holder.title != null){
                //set title and match photo width
                holder.title.setText(item.title);
            }

            if(holder.shop != null){
                //set shop name
                holder.shop.setText(item.shop);
            }

            if(holder.price != null){
                //set price
                holder.price.setText("$" + item.price);
            }
        }
        return view;
    }

    //AsyncTask to retrieve and store bitmaps
    private class LoadPictures extends AsyncTask<Item, Void, Bitmap> {

        //Variables
        View view;
        ImageView picture;

        //Constructor
        LoadPictures(View view, ImageView picture){
            this.view = view;
            this.picture = picture;
        }

        @Override
        protected Bitmap doInBackground(Item... params) {
            //Avatar and inline photo
            Bitmap result = null;

            try {
                //Get the avatar bitmap, every tweet MUST have one
                URL picture_url = new URL(params[0].url);
                result = BitmapFactory.decodeStream(picture_url.openConnection().getInputStream());

                //Set the Bitmaps to the Tweet object so they don't have to be reloaded.
                params[0].setPicture(result);


            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //This method puts us back on the main thread, so we can update the views with the newly loaded bitmaps
            if(result != null) {
                picture.setImageBitmap(result);
            }
        }

        @Override
        protected void onPreExecute() {
            //Do nothing
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Do nothing
        }
    }

    class ViewHolder{
        ImageView photo;
        TextView title;
        TextView shop;
        TextView price;
    }
}
