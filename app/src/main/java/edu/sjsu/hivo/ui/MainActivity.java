package edu.sjsu.hivo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AutocompleteResultType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.adapter.PropertyListAdapter;
import edu.sjsu.hivo.events.DetailActivityData;
import edu.sjsu.hivo.events.MainActivityData;
import edu.sjsu.hivo.model.Property;
import edu.sjsu.hivo.networking.VolleyNetwork;
import edu.sjsu.hivo.ui.propertydetail.PropertyDetail;
import edu.sjsu.hivo.ui.utility.FilterUtility;
import edu.sjsu.hivo.ui.utility.SortUtility;

public class MainActivity extends AppCompatActivity  {
    String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private PropertyListAdapter adapter;
    private List<Property> propertyList;
    private TextView mapTextView;
    private ImageView mapImageView;
    private static final int TAG_CODE_PERMISSION_LOCATION = 110;
    private String response="";
    private PlacesAutocompleteTextView userInput;
    private String userText, extension;
    private ImageView filterImg,sortImg;
    private TextView filterText,sortText;
    private FilterUtility filterUtility;
    private SortUtility sortUtility;
    private Intent filterIntent = null;
    private Intent sortIntent = null;
    static final int PICK_FILTER_REQUEST = 1;  // The request code
    static final int PICK_SORT_REQUEST = 2;  // The request code
    static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean allPropertiesLoaded;
    private boolean isFetching;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filterIntent = FilterUtility.getFilters(getApplicationContext());
        propertyList = new ArrayList<>();

        initUI();
        checkPermission();
        restoreFromInstance(savedInstanceState);
        if (propertyList.size() == 0 || TextUtils.isEmpty(extension)) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            Log.d(TAG, "found last location");
                            if (location != null) {
                                extension = "cordinate?longitude="+String.valueOf(location.getLongitude())+"&latitude="+String.valueOf(location.getLatitude());
                                sendRequestAndprintResponse(extension,0, true);
                            }
                        }
                    });
        }

        userInput.getText().clear();
        userInput.clearFocus();
        userInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput.setText("");
            }
        });
        filterUtility = new FilterUtility(this);
        filterUtility.setFilterListener(filterImg, filterText);

        sortUtility = new SortUtility(this);
        sortUtility.setSortListener(sortImg,sortText);

        setAutoPlaceComplete();
        showMapButtonClickListeners();

        adapter = new PropertyListAdapter(propertyList,this);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && !allPropertiesLoaded) {
                    Log.d(TAG, "end of list reached in recycler view, may be load more from server.");
                    sendRequestAndprintResponse(extension, propertyList.size(), false);
                }
            }
        });


    }

    private void initUI(){
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (propertyList == null || propertyList.size() == 0) {
                    sendRequestAndprintResponse(extension, 0, false);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        recyclerView = findViewById(R.id.property_details_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        filterImg = findViewById(R.id.list_filter_iv);
        filterText = findViewById(R.id.list_filter_tv);
        sortImg = findViewById(R.id.list_sort_iv);
        sortText = findViewById(R.id.list_sort_tv);
        userInput =  findViewById(R.id.enter_location);
        userText = String.valueOf(userInput.getText());
        mapTextView = (TextView) findViewById(R.id.list_map_tv);
        mapImageView = (ImageView) findViewById(R.id.list_map_iv);
    }

    // TODO(shefali@) Fix handling result in onActivityResult
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "NO PERMISSION GRANTED", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void setAutoPlaceComplete(){
        userInput.setResultType(AutocompleteResultType.GEOCODE);
        userInput.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(@NonNull final Place place) {
                        hideKeyboard(MainActivity.this);
                        userInput.clearFocus();
                        userInput.getText().clear();
                        userInput.getDetailsFor(place, new DetailsCallback() {
                            @Override
                            public void onSuccess(final PlaceDetails details) {
                                Log.d(TAG, "details " + details);
                                Log.d(TAG, " Details: " + details.address_components.toString());
                                Log.d(TAG, details.types.toString());

                                if (details.types.contains("street_address") || details.types.contains("premise")) {
                                    String address = details.address_components.get(0).short_name+" "+ /*street No*/
                                            details.address_components.get(1).short_name;/*Adddress Line 1*/

                                    try {
                                        address = URLEncoder.encode(address, "UTF-8");
                                        // don't update the extension private variable here
                                        String extension = "/data?str=" + address;
                                        sendRequestAndprintResponse(extension,0, false);
                                        userInput.clearFocus();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    double lat = details.geometry.location.lat;
                                    double lng = details.geometry.location.lng;
                                    extension = "cordinate?longitude="+String.valueOf(lng)+"&latitude="+String.valueOf(lat);
                                    sendRequestAndprintResponse(extension,0, true);
                                }
                            }

                            @Override
                            public void onFailure(final Throwable failure) {
                                Log.d("test", "failure " + failure);
                            }
                        });
                    }
                });
    }


    private void showMapButtonClickListeners() {
        mapImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        mapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FILTER_REQUEST:
                if (resultCode == FilterActivity.RESULT_OK) {
                    filterIntent = data;
                    sendRequestAndprintResponse(extension,0, true);
                    break;

                }
            case PICK_SORT_REQUEST:
                super.onActivityResult(requestCode, resultCode, data);
                if (resultCode == SortActivity.RESULT_OK) {
                    sortIntent = data;
                    sendRequestAndprintResponse(extension,0, true);
                }
        }
    }

    public void sendRequestAndprintResponse(String extension, final int skipCount, boolean clearList) {
        if (isFetching) return;
        isFetching = true;
        swipeRefreshLayout.setRefreshing(true);

        extension += "&skip="+skipCount;
        extension = FilterUtility.applyFilterData(filterIntent, extension);
        extension = SortUtility.applySortData(sortIntent, extension);
        String url = VolleyNetwork.AWS_ENDPOINT + extension;
        Log.d(TAG, "requesting data from url" + url);

        // if it is a new request, clear old data
        if (clearList) {
            allPropertiesLoaded = false;
            propertyList.clear();
            adapter.notifyDataSetChanged();
        }

        try {
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, "response is:" + response.toString());
                            Type listType = new TypeToken<ArrayList<Property>>() {}.getType();
                            List<Property> list = new Gson().fromJson(response.toString(), listType);
                            //Log.d(TAG, "response from url: " + url);

                             if (list.size() == 1 && skipCount == 0) {
                                DetailActivityData detailActivityData = new DetailActivityData(list.get(0));
                                EventBus.getDefault().postSticky(detailActivityData);
                                Intent intent = new Intent(MainActivity.this, PropertyDetail.class);
                                startActivity(intent);
                            } else if (list.size() > 0) {
                                propertyList.addAll(list);
                                adapter.notifyDataSetChanged();
                            } else if (skipCount == 0) {
                                Toast.makeText(getApplicationContext(), "No properties found, please try a different search.", Toast.LENGTH_LONG).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            isFetching = false;
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error == null || error.networkResponse == null) {
                                if (error != null) Log.e(TAG, "..", error);
                                return;
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            isFetching = false;
                            Toast.makeText(getApplicationContext(), "Request failed, please pull to refresh to try again.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "error making server request" + error.getMessage());
                        }
                    }
            );
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VolleyNetwork
                    .getInstance(getApplicationContext())
                    .getRequestQueue(getApplicationContext())
                    .add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (propertyList != null) {
            EventBus.getDefault().postSticky(new MainActivityData(propertyList));
        }

        bundle.putString("extension", extension);
        bundle.putParcelable("filters", filterIntent);
        bundle.putParcelable("sort", sortIntent);
    }

    private void restoreFromInstance(Bundle bundle){
        if (bundle != null) {
            MainActivityData data = EventBus.getDefault().getStickyEvent(MainActivityData.class);
            if (data!= null && data.getProperties() != null ) {
                propertyList.addAll(data.getProperties());
            }

            extension = bundle.getString("extension");
            filterIntent = bundle.getParcelable("filters");
            sortIntent = bundle.getParcelable("sort");
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
