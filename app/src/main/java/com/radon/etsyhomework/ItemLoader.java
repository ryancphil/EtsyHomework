package com.radon.etsyhomework;

import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class handles the actual API call that retrieves items.
 */
public class ItemLoader extends AsyncTaskLoader<ArrayList<Item>>{

    //url containing the specific API call.
    String url;

    public ItemLoader(Context context, String url) {
        super(context);
        this.url = url;
    }


    @Override
    public ArrayList<Item> loadInBackground() {

        //Make a get request
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        Log.e("Loader:URL",url);

        try{
            HttpResponse resp = httpClient.execute(get);
            StatusLine statusLine = resp.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if(statusCode != 200){
                Log.e("ItemLoader", "statusCode != 200");
                return null;
            }else{
                //Use a string buffer to read in the JSON information
                InputStream jsonStream = resp.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                }
                String jsonData = builder.toString();
                //Log.e("ItemLoader:jsonData", jsonData); //TESTING ONLY

                //Send JSON data to parse method
                ArrayList<Item> results = parse(jsonData);
                //Return arrayList of Item objects
                return results;

            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Item> parse(String data){
        String title = "";
        String url = "";

        String price;
        String shop;
        String description;

        ArrayList<Item> results = new ArrayList<>();

        try{
            //Convert string data to JSON object for easy parsing
            JSONObject json = new JSONObject(data);
            Log.e("JSON: ", json.toString());
            JSONArray jsonArray = json.getJSONArray("results");

            for(int i = 0; i < jsonArray.length(); i++){
                title = jsonArray.getJSONObject(i).getString("title");
                url = jsonArray.getJSONObject(i).getJSONObject("MainImage").getString("url_570xN");
                price = jsonArray.getJSONObject(i).getString("price");
                shop = jsonArray.getJSONObject(i).getJSONObject("Shop").getString("shop_name");
                description = jsonArray.getJSONObject(i).getString("description");

                //create a new Item object using data retrieved and add it to arrayList results
                results.add(new Item(title,url,price,shop,description));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        //Return arraylist of Item objects
        return results;
    }

}
