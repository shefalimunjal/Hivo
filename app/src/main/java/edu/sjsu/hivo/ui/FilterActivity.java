package edu.sjsu.hivo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.ui.utility.FilterUtility;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView priceTv, priceAny, sqftTv, sqftAny;
    private Spinner priceMax, priceMin, sqftMax, sqftMin;
    private Button bedMinus, bedPlus, bathMinus, bathPlus, applyFilter;
    private EditText noOfBeds, noOfBath;
    private int beds=0;
    String maxPriceRange="", minPriceRange="", maxSqftRange="", minSqftRange="";

    private double baths;
    private ArrayAdapter<CharSequence> minAdapter, maxAdapter, minSqAdapter, maxSqAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        priceTv = (TextView)findViewById(R.id.price_tv);
        priceAny = (TextView)findViewById(R.id.price_any);
        sqftTv = (TextView)findViewById(R.id.sqft_tv);
        sqftAny = (TextView)findViewById(R.id.sqft_any);

        bedMinus = (Button)findViewById(R.id.bed_minus_btn);
        bedPlus = (Button)findViewById(R.id.bed_plus_btn);
        bathMinus = (Button)findViewById(R.id.bath_minus_btn);
        bathPlus = (Button)findViewById(R.id.bath_plus_btn);
        noOfBath = (EditText)findViewById(R.id.no_of_bath);
        noOfBeds = (EditText)findViewById(R.id.no_of_beds);

        applyFilter = (Button)findViewById(R.id.apply_filter_button);
        priceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPriceData();
            }
        });
        priceAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPriceData();
            }
        });
        sqftTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSqftData();
            }
        });
        sqftAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSqftData();
            }
        });

        bedMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBeds();
            }
        });
        bedPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBeds();
            }
        });
        bathMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeBaths();
            }
        });
        bathPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBaths();
            }
        });

        applyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyUserFilters();
            }
        });

        loadDataFromSharedPreferences();
    }

    private void loadDataFromSharedPreferences() {
        Intent filterIntent = FilterUtility.getFilters(getApplicationContext());
        if (filterIntent != null && filterIntent.getStringExtra("MAX_PRICE") != null) {
            maxPriceRange = filterIntent.getStringExtra("MAX_PRICE");
            minPriceRange = filterIntent.getStringExtra("MIN_PRICE");
            maxSqftRange = filterIntent.getStringExtra("MAX_SQFT");
            minSqftRange = filterIntent.getStringExtra("MIN_SQFT");

            beds = Integer.parseInt(filterIntent.getStringExtra("NO_OF_BEDS"));
            noOfBeds.setText(String.valueOf(beds));

            baths = Double.parseDouble(filterIntent.getStringExtra("NO_OF_BATHS"));
            noOfBath.setText(String.valueOf(String.valueOf(baths)));
        }
    }

    private void removeBeds(){
        String noBed = noOfBeds.getText().toString();
        noBed = noBed.trim();

        if (noBed.equals("Studio")) {
            beds = 0;
        } else if (noBed.equals("")) {
            beds = 0;
            noOfBeds.setText("Studio");
        }
        else {
            beds = Integer.parseInt(noBed);
            if (beds > 1) {
                beds--;
                noOfBeds.setText(String.valueOf(beds));
            } else {
                noBed = "Studio";
                noOfBeds.setText(noBed);
            }
        }
    }

    private void addBeds(){
        String noBed = noOfBeds.getText().toString();
        if (noBed.equals("") ){
            noBed = "Studio";
            noOfBeds.setText(String.valueOf(noBed));
            beds = 0;
        }
        else if (noBed.equals("Studio")){
            beds = 1;
            noOfBeds.setText(String.valueOf(beds));
        }
        else {
            noBed = noBed.trim();
            beds = !noBed.equals("")?Integer.parseInt(noBed) : 0;
//                    beds = Integer.parseInt(noBed);
            beds++;
            noOfBeds.setText(String.valueOf(beds));
        }
    }

    private void removeBaths(){
        String noBath = noOfBath.getText().toString();
        noBath = noBath.trim();

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (noBath.equals("")) {
            baths = 1;
        }
        else {
            noBath = noBath.substring(0, noBath.length() - 1);
            baths = Double.parseDouble(noBath);
            if (baths > 1) {
                baths = baths-0.5;
            }
        }
        noOfBath.setText(String.valueOf(format.format(baths))+"+");
    }


    private void addBaths(){
        String noBath = noOfBath.getText().toString();
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        if (noBath.equals("") ){
            baths = 1;
            noOfBath.setText(String.valueOf(format.format(baths))+"+");
        }
        else {
            noBath = noBath.trim();
            noBath = noBath.substring(0, noBath.length() - 1);
            baths =  Double.parseDouble(noBath);
            baths = baths+ 0.5;
            noOfBath.setText(String.valueOf(format.format(baths))+"+");
        }
    }

    private void getPriceData(){
        priceMax = (Spinner)findViewById(R.id.price_max_spinner);
        priceMin = (Spinner)findViewById(R.id.price_min_spinner);

        priceAny.setVisibility(View.INVISIBLE);
        priceMax.setVisibility(View.VISIBLE);
        priceMin.setVisibility(View.VISIBLE);

        priceMax.setOnItemSelectedListener(this);
        priceMin.setOnItemSelectedListener(this);

        maxAdapter = ArrayAdapter.createFromResource(this,
                R.array.max_price, android.R.layout.simple_spinner_item);

        maxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceMax.setAdapter(maxAdapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
        minAdapter = ArrayAdapter.createFromResource(this,
                R.array.min_price, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        maxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        priceMin.setAdapter(minAdapter);
    }

    private void getSqftData(){
        sqftMax = (Spinner)findViewById(R.id.sqft_max_spinner);
        sqftMin = (Spinner)findViewById(R.id.sqft_min_spinner);

        sqftAny.setVisibility(View.INVISIBLE);
        sqftMax.setVisibility(View.VISIBLE);
        sqftMin.setVisibility(View.VISIBLE);

        sqftMax.setOnItemSelectedListener(this);

        maxSqAdapter = ArrayAdapter.createFromResource(this,
                R.array.max_sqft, android.R.layout.simple_spinner_item);

        maxSqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sqftMax.setAdapter(maxSqAdapter);

        sqftMin.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        minSqAdapter = ArrayAdapter.createFromResource(this,
                R.array.min_sqft, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        maxSqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sqftMin.setAdapter(minSqAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int maxRange, minRange;
        Spinner spinner = (Spinner) parent;
        String item = parent.getItemAtPosition(position).toString();

        switch (spinner.getId()){
            case R.id.price_max_spinner:
                if (!item.equals("No Max")) {
                    maxPriceRange = item;
                    maxPriceRange  = maxPriceRange.replace(",","");
                    maxPriceRange = maxPriceRange.replace("$","");
                    maxPriceRange.trim();
                    if (minPriceRange != "") {
                        minPriceRange = minPriceRange.replace(",","");
                        minPriceRange = minPriceRange.replace("$","");
                        minPriceRange = minPriceRange.trim();

                        maxRange = maxPriceRange.equals("")?Integer.MAX_VALUE:Integer.parseInt(maxPriceRange);
                        minRange = maxPriceRange.equals("")?Integer.MIN_VALUE:Integer.parseInt(minPriceRange);
                        if (maxRange < minRange) {
                            Toast.makeText(getApplicationContext(), "Select a value more than " + minPriceRange,
                                    Toast.LENGTH_SHORT).show();
                            maxPriceRange = "";
                            priceMax.setAdapter(maxAdapter);
                        }
                    }
                } else {
                    maxPriceRange = "";
                }
                break;
            case R.id.price_min_spinner:
                if (!item.equals("No Min")) {
                    minPriceRange = item;
                    minPriceRange = minPriceRange.replace(",","");
                    minPriceRange = minPriceRange.replace("$","");
                    minPriceRange = minPriceRange.trim();
                    if (maxPriceRange.equals("")) {
                        maxPriceRange = maxPriceRange.replace(",","");
                        maxPriceRange = maxPriceRange.replace("$","");

                        maxRange = maxPriceRange.equals("") ? Integer.MAX_VALUE : Integer.parseInt(maxPriceRange);
                        minRange = maxPriceRange.equals("") ? Integer.MIN_VALUE : Integer.parseInt(minPriceRange);
                        maxPriceRange = maxPriceRange.trim();
                        Log.d("TAG","maxPriceRange "+maxPriceRange+" minPriceRange "+minPriceRange);
                        if (maxRange < minRange) {
                            Toast.makeText(getApplicationContext(), "Select a value less than " + maxPriceRange,
                                    Toast.LENGTH_SHORT).show();
                            maxPriceRange = "";
                            priceMin.setAdapter(minAdapter);
                        }
                    }
                } else {
                    minPriceRange = "";
                }
                break;
            case R.id.sqft_max_spinner:
                if (!item.equals("No Max")) {
                    maxSqftRange = item;
                    if (minSqftRange != ""){
                        if(Integer.parseInt(minSqftRange) > Integer.parseInt(maxSqftRange)){
                            Toast.makeText(getApplicationContext(), "Select a value more than " + minSqftRange,
                                    Toast.LENGTH_SHORT).show();
                            maxSqftRange = "";
                            sqftMax.setAdapter(maxSqAdapter);
                        }
                    }
                } else {
                    maxSqftRange = "";
                }
                break;
            case R.id.sqft_min_spinner:
                if (!item.equals("No Min")) {
                    minSqftRange = item;
                    if (maxSqftRange != ""){
                        if(Integer.parseInt(minSqftRange) > Integer.parseInt(maxSqftRange)){
                            Toast.makeText(getApplicationContext(), "Select a value less than " + maxSqftRange,
                                    Toast.LENGTH_SHORT).show();
                            minSqftRange = "";
                            sqftMin.setAdapter(minSqAdapter);
                        }
                    }
                } else {
                    minSqftRange = "";
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void applyUserFilters(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("MAX_PRICE", String.valueOf(maxPriceRange));
        resultIntent.putExtra("MIN_PRICE", String.valueOf(minPriceRange));
        resultIntent.putExtra("MAX_SQFT", String.valueOf(maxSqftRange));
        resultIntent.putExtra("MIN_SQFT", String.valueOf(minSqftRange));
        resultIntent.putExtra("NO_OF_BEDS", String.valueOf(beds));
        resultIntent.putExtra("NO_OF_BATHS", String.valueOf(baths));
        FilterUtility.saveFilters(getApplicationContext(), resultIntent);
        setResult(FilterActivity.RESULT_OK, resultIntent);
        finish();
    }
}
