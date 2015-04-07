package com.radon.etsyhomework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * MainActivity contains the sorting spinner, keyword search input, and ListFragment that displays the items.
 *
 * @author Anon at ETSY's request.
 * [12.16.14]
 * */
public class MainActivity extends FragmentActivity {

    ItemFragment frag;
    int spinner_pos = 0;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frag = (ItemFragment) getFragmentManager().findFragmentById(R.id.item_frag);
        alertDialog = new AlertDialog.Builder(this).create();

        //EditText box that lets users search ETSY by keywords
        final EditText editText = (EditText)findViewById(R.id.search);
        //Prevent keyboard from auto appearing
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String keyword = editText.getText().toString();
                    //restart loader in the ItemFragment using users input IF they have actually provided input.
                    if(keyword != null && keyword.length() > 0) {
                        frag.reloadData(keyword);
                    }
                    //hide keyboard when user presses done/go/submit
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        //Spinner lets user choose how to sort search results
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_pos = position;
                //Determine what position spinner is in and sort.
                if(position == 0){
                    frag.sortByTitle();
                }else if (position == 1){
                    frag.sortByShop();
                }else{
                    frag.sortByPrice();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Default sort by title
                frag.sortByTitle();
            }
        });
    }

    public void prompt(Item item){
        // Display Description
        alertDialog.setTitle("Description:");
        alertDialog.setMessage(item.description);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
