package com.radon.etsyhomework;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class is the fragment that displays the listview of items from ETSY's API calls.
 * By default it will load the newest listings, but when the user makes a search it will
 * refresh making a new call using the keywords.
 *
 * NOTE: Having a loader grab all of the item data all at once makes the user wait to
 * see their search results. A different implementation (like the actual ETSY app uses) to
 * load data quickly and show pictures as needed is preferable.
 */
public class ItemFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<Item>> {

    ListView items;
    ArrayList<Item> data = new ArrayList<>();
    ItemAdapter adapter;
    ItemLoader loader;
    //ProgressDialog progressDialog;

    //Default api call which includes MainImage and Shop
    static String url = "https://api.etsy.com/v2/listings/active?api_key=liwecjs0c3ssk6let4p1wqt9&includes=MainImage,Shop";

    public ItemFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the fragment
        View view =  inflater.inflate(R.layout.fragment_item, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ItemAdapter(getActivity(), android.R.layout.simple_list_item_1, data);
        items = getListView();

        //Check savedInstanceState
        if(savedInstanceState == null){
            getLoaderManager().initLoader(0, null, ItemFragment.this).forceLoad();
        }else{
            //A loader exists so repopulate the listview from savedInstanceState
            data.clear();
            data = savedInstanceState.getParcelableArrayList("key");
            adapter = new ItemAdapter(getActivity(), android.R.layout.simple_list_item_1, data);
            adapter.notifyDataSetChanged();
        }


        items.setAdapter(adapter);
        items.setDividerHeight(1);


        //Item Click listener that displays/hides the description of the item clicked.
        items.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = adapter.getItem(position);
                ((MainActivity)getActivity()).prompt(item);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<ArrayList<Item>> onCreateLoader(int i, Bundle bundle) {
        //Create a new loader
        loader = new ItemLoader(this.getActivity(),url);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Item>> loader, ArrayList<Item> data) {
        //update the data array with newly fetched items
        this.data.clear();
        this.data.addAll(data);
        //Sort the new data under the same filter previously selected
        if(((MainActivity)getActivity()).spinner_pos == 0){
            sortByTitle();
        }else if(((MainActivity)getActivity()).spinner_pos == 1){
            sortByShop();
        }else{
            sortByPrice();
        }

        adapter.notifyDataSetChanged();

        //Load is finished, dismiss progress dialog and allow screen rotation
        //progressDialog.dismiss();
        ((TextView)getActivity().findViewById(R.id.progress)).setText("No Matches.");
        ((ProgressBar)getActivity().findViewById(R.id.progressBar1)).setVisibility(View.INVISIBLE);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        //Destroy the loader when it's done its job.
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Item>> loader) {
        Log.e("RESET:", "Loader Reset!");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save items so loader doesn't get reset every time the screen changes
        outState.putParcelableArrayList("key", data);
    }

    //Method is called when the user searches keywords
    public void reloadData(String keyword){
        if(keyword == null){
            url = "https://api.etsy.com/v2/listings/active?api_key=liwecjs0c3ssk6let4p1wqt9&includes=MainImage,Shop";
        }else {
            //Handle leading/trailing spaces
            keyword = keyword.trim();
            //replaces spaces with commas
            keyword = keyword.replaceAll(" ", ",");
            //Reset the url to make a new api call including the users input keywords
            url = "https://api.etsy.com/v2/listings/active?api_key=liwecjs0c3ssk6let4p1wqt9&includes=MainImage,Shop&keywords=" + keyword;
        }
        //Restart the loader
        this.data.clear();
        adapter.notifyDataSetChanged();
        items.invalidateViews();
        ((TextView)getActivity().findViewById(R.id.progress)).setText("Loading...");
        ((ProgressBar)getActivity().findViewById(R.id.progressBar1)).setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(0,null,ItemFragment.this).forceLoad();
    }

    /******************
    * SORTING METHODS
    *******************/
    //Sort items alphabetically by title
    public void sortByTitle(){
        if (data == null){
            return;
        }
        Log.e("Data array", data.toString());
        Collections.sort(data,new Comparator<Item>() {
            @Override
            public int compare(Item p1, Item p2) {
                //implement your compare method in here
                if(p1 != null && p2 != null){
                    return p1.title.compareTo(p2.title);
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        items.invalidateViews();

    }

    //Sort items alphabetically by shop name
    public void sortByShop(){
        if (data == null){
            return;
        }
        Collections.sort(data,new Comparator<Item>() {
            @Override
            public int compare(Item p1, Item p2) {
                //implement your compare method in here
                if(p1 != null && p2 != null) {
                    return p1.shop.compareTo(p2.shop);
                }
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        items.invalidateViews();
    }

    //Compare items by their price and sort them from cheapest to most expensive
    public void sortByPrice(){
        if (data == null){
            return;
        }
        Collections.sort(data,new Comparator<Item>() {
            @Override
            public int compare(Item p1, Item p2) {
                //implement your compare method in here
                if(p1 != null && p2 != null) {
                    double price1 = Double.parseDouble(p1.price);
                    double price2 = Double.parseDouble(p2.price);
                    if (price1 >= price2) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
                return 0;
            }
        });
        adapter = new ItemAdapter(getActivity(), android.R.layout.simple_list_item_1, data);
        adapter.notifyDataSetChanged();
        items.invalidateViews();
    }

}
