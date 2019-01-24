package edu.sjsu.hivo.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.sjsu.hivo.events.DetailActivityData;
import edu.sjsu.hivo.model.Property;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
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
import edu.sjsu.hivo.networking.VolleyNetwork;
import edu.sjsu.hivo.ui.propertydetail.PropertyDetail;
import edu.sjsu.hivo.ui.utility.FilterUtility;
import edu.sjsu.hivo.ui.utility.SortUtility;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraIdleListener {
    private GoogleMap googleMap;
    private LatLng currentLocation;
    LocationManager locationManager;
    private static final int TAG_CODE_PERMISSION_LOCATION = 110;
    private List<Property> propertyList = new ArrayList<>();
    String TAG = MapActivity.class.getSimpleName();
    private TextView mapTextView;
    private ImageView mapImageView;
    private IconGenerator iconGen;
    private Gson gson = new Gson();
    Marker myMarker;
    private PlacesAutocompleteTextView userInput;
    private String response;
    private ImageView filterImg,sortImg;
    private TextView filterText,sortText;
    private FilterUtility filterUtility;
    private SortUtility sortUtility;
    static final int PICK_SORT_REQUEST = 2;  // The request code
    static final int PICK_FILTER_REQUEST = 1;  // The request code
    private FusedLocationProviderClient mFusedLocationClient;
    private Intent filterIntent = null;
    private Intent sortIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_property_listing);
        checkPermission();
        restoreFromBundle(savedInstanceState);
        mapTextView = (TextView)findViewById(R.id.list_map_tv);
        mapImageView = (ImageView)findViewById(R.id.list_map_iv);
        userInput = (PlacesAutocompleteTextView)findViewById(R.id.enter_location);
        userInput.clearFocus();
        userInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInput.setText("");
            }
        });
        filterImg = (ImageView)findViewById(R.id.list_filter_iv);
        filterText = (TextView)findViewById(R.id.list_filter_tv);

        //sortImg = findViewById(R.id.list_sort_iv);
        //sortText = findViewById(R.id.list_sort_tv);

        filterIntent = FilterUtility.getFilters(getApplicationContext());

        filterUtility = new FilterUtility(this);
        filterUtility.setFilterListener(filterImg, filterText);

        //sortUtility = new SortUtility(this);
        //sortUtility.setSortListener(sortImg, sortText);

        setShowListViewClickListener();
        iconGen = new IconGenerator(this);
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.onCreate(null);
            mapFragment.onResume();
            mapFragment.getMapAsync(this);
        }

        setAutoPlaceCompleteLIstener();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (currentLocation == null) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            if (googleMap != null) {
                                Log.i(TAG, "CurrentLocation is... " + currentLocation);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            }
                        }
                    });
        } else if (googleMap != null){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    private void setAutoPlaceCompleteLIstener(){
        userInput.setResultType(AutocompleteResultType.GEOCODE);
        userInput.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(@NonNull final Place place) {
                        MainActivity.hideKeyboard(MapActivity.this);
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
                                        String extension = "/data?str="+address;
                                        sendRequestAndprintResponse(extension);
                                        userInput.clearFocus();
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    currentLocation = new LatLng(details.geometry.location.lat, details.geometry.location.lng);
                                    if (googleMap != null) {
                                        Log.i(TAG, "CurrentLocation is... " + currentLocation);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                                    }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG,"inside OnMapReady" );
        MapsInitializer.initialize(this);
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraIdleListener(this);
        if (currentLocation != null) {
            Log.i(TAG,"inside mapReady location is " +currentLocation);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    private void updateMap() {
        Log.i(TAG,"inside updateMap()" );
        if (googleMap == null) return;

        for(int i = 0; i< propertyList.size(); i++){
            if (propertyList.get(i).isLocationAvailable()) {
                myMarker = createMarker(propertyList.get(i).getLatitude(), propertyList.get(i)
                                .getLongitude(), propertyList.get(i).getFormattedPriceForMap(),
                        propertyList.get(i).getAddress(), propertyList.get(i).getSaleType());
                myMarker.setTag(propertyList.get(i));
            }
        }
    }


    protected Marker createMarker(double latitude, double longitude, String price, String address, String saleType) {
        if(saleType.equals("Sold")) {
            iconGen.setStyle(IconGenerator.STYLE_RED);
        }
        else {
            iconGen.setStyle(IconGenerator.STYLE_GREEN);
        }

        iconGen.setTextAppearance(R.style.myStyleText);
        myMarker =  googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(price)
                .snippet(address)
                .icon(BitmapDescriptorFactory.fromBitmap(iconGen.makeIcon(price)))
                .anchor(iconGen.getAnchorU(), iconGen.getAnchorV()));

        return myMarker;

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        gotToDetailPageWhenClicked(marker);
        return true;
    }

    private void gotToDetailPageWhenClicked(Marker marker){

        Intent intent = new Intent(this, PropertyDetail.class);
        DetailActivityData detailActivityData = new DetailActivityData((Property) marker.getTag());
        EventBus.getDefault().postSticky(detailActivityData);
        this.startActivity(intent);

    }

    @Override
    public void onLocationChanged(Location location) {
        googleMap.clear();
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title("i'm here");
        googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    public void sendRequestAndprintResponse(String extension) {
        extension += "&skip=0";
        extension = FilterUtility.applyFilterData(filterIntent, extension);
        //extension = SortUtility.applySortData(sortIntent, extension);
        final String url = VolleyNetwork.AWS_ENDPOINT + extension;
        Log.d(TAG, "fetching data from url:  " + url);

        try{
            JsonArrayRequest request = new JsonArrayRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONArray>() {
                        public void onResponse(JSONArray response){
                            Log.d(TAG,"response is:" + response.toString());
                            Type listType = new TypeToken<ArrayList<Property>>(){}.getType();
                            List<Property> list = new Gson().fromJson(response.toString(), listType);
                            if (list.size() >  1) {
                                googleMap.clear();
                                propertyList.clear();
                                propertyList.addAll(list);
                                updateMap();
                            } else if (list.size() ==  1){
                                DetailActivityData detailActivityData = new DetailActivityData(propertyList.get(0));
                                EventBus.getDefault().postSticky(detailActivityData);
                                Intent intent = new Intent(MapActivity.this, PropertyDetail.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "No properties found, please try a different search", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error == null || error.networkResponse == null) {
                                return;
                            }
                            Toast.makeText(getApplicationContext(), "Request failed, please try again later.", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "error making server request"+error.getMessage());
                            Toast.makeText(MapActivity.this, "error: "+error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyNetwork
                    .getInstance(getApplicationContext())
                    .getRequestQueue(this.getApplicationContext())
                    .add(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FILTER_REQUEST:
                if (resultCode == FilterActivity.RESULT_OK) {
                    filterIntent = data;
                    onCameraIdle();
                    break;

                }
//            case PICK_SORT_REQUEST:
//                super.onActivityResult(requestCode, resultCode, data);
//                if (resultCode == SortActivity.RESULT_OK) {
//                    sortIntent = data;
//                    onCameraIdle();
//                }
        }
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    TAG_CODE_PERMISSION_LOCATION);
        }

    }

    private void setShowListViewClickListener() {
        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        mapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCameraIdle() {
        googleMap.clear();
        currentLocation = googleMap.getCameraPosition().target;
        Log.d(TAG, "map has stopped moving, current location: " + currentLocation);
        String extension = "cordinate?longitude="+String.valueOf(currentLocation.longitude)+"&latitude="+String.valueOf(currentLocation.latitude);
        Log.d(TAG,"inside  Camera Idle" +response);
        sendRequestAndprintResponse(extension);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putDouble("lat", currentLocation.latitude);
        bundle.putDouble("lng", currentLocation.longitude);
        bundle.putParcelable("filters", filterIntent);
        //bundle.putParcelable("sort", sortIntent);
    }

    private void restoreFromBundle(Bundle bundle) {
        if (bundle != null) {
            double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("lng");
            currentLocation = new LatLng(lat, lng);
            filterIntent = bundle.getParcelable("filters");
           // sortIntent = bundle.getParcelable("sort");
        }
    }
}

