package edu.sjsu.hivo.ui.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.sjsu.hivo.ui.FilterActivity;


public class FilterUtility {

    private static final int PICK_FILTER_REQUEST = 1;  // The request code

    private Activity listOrMapActivity;

    public FilterUtility(Activity activity) {
        listOrMapActivity = activity;
    }

    public void setFilterListener(ImageView filterImg, TextView filterText) {
        filterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent filterIntent = new Intent(context , FilterActivity.class);
                listOrMapActivity.startActivityForResult(filterIntent,PICK_FILTER_REQUEST);
            }
        });

        filterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent filterIntent = new Intent(context , FilterActivity.class);
                listOrMapActivity.startActivityForResult(filterIntent,PICK_FILTER_REQUEST);
            }
        });
    }


    public static String applyFilterData(Intent data, String extension){
        if (data == null || TextUtils.isEmpty(extension)) {
            return extension;
        }
        String  maxPrice, minPrice, maxSqft, minSqft, noOfBeds, noOfBaths;
        maxPrice= data.getStringExtra("MAX_PRICE");
        if (!maxPrice.equals("") ){
            extension += "&max_price=" + maxPrice;
        }
        minPrice = data.getStringExtra("MIN_PRICE");
        if (!minPrice.equals("")) {
            extension += "&min_price=" + minPrice;
        }
        maxSqft = data.getStringExtra("MAX_SQFT");
        if (!maxSqft.equals("")) {
            extension += "&max_sqft=" + maxSqft;
        }
        minSqft = data.getStringExtra("MIN_SQFT");
        if (!minSqft.equals("")) {
            extension += "&min_sqft=" + minSqft;
        }
        noOfBeds = data.getStringExtra("NO_OF_BEDS");
        if (!noOfBeds.equals("0")) {
            Log.d("TEST","noOfBeds "+noOfBeds);
            extension += "&beds=" + noOfBeds + "&beds_op=eq";
        }
        noOfBaths = data.getStringExtra("NO_OF_BATHS");
        if (!noOfBaths.equals("0.0")) {
            Log.d("TEST","noOfBaths "+noOfBaths);
            extension += "&baths=" + noOfBaths + "&baths_op=eq";
        }

        Log.d("TEST","Extension"+extension);
        Log.i("**TAG*************",maxPrice+" "+minPrice+" "+maxSqft+" "+ minSqft+" "+noOfBeds+" "+noOfBaths);
        return extension;

    }

    public static void saveFilters(Context context, Intent filterIntent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("filters", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MAX_PRICE", filterIntent.getStringExtra("MAX_PRICE"));
        editor.putString("MIN_PRICE", filterIntent.getStringExtra("MIN_PRICE"));
        editor.putString("MAX_SQFT", filterIntent.getStringExtra("MAX_SQFT"));
        editor.putString("MIN_SQFT", filterIntent.getStringExtra("MIN_SQFT"));
        editor.putString("NO_OF_BEDS", filterIntent.getStringExtra("NO_OF_BEDS"));
        editor.putString("NO_OF_BATHS", filterIntent.getStringExtra("NO_OF_BATHS"));
        editor.apply();
    }

    public static Intent getFilters(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("filters", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("MAX_PRICE")) {
            Intent filterIntent = new Intent();
            filterIntent.putExtra("MAX_PRICE", sharedPreferences.getString("MAX_PRICE", ""));
            filterIntent.putExtra("MIN_PRICE", sharedPreferences.getString("MIN_PRICE", ""));
            filterIntent.putExtra("MAX_SQFT", sharedPreferences.getString("MAX_SQFT", ""));
            filterIntent.putExtra("MIN_SQFT", sharedPreferences.getString("MIN_SQFT", ""));
            filterIntent.putExtra("NO_OF_BEDS", sharedPreferences.getString("NO_OF_BEDS", ""));
            filterIntent.putExtra("NO_OF_BATHS", sharedPreferences.getString("NO_OF_BATHS", ""));
            return filterIntent;
        }

        return null;
    }
}
