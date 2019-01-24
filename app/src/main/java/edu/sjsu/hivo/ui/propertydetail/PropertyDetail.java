package edu.sjsu.hivo.ui.propertydetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyPermission;

import edu.sjsu.hivo.events.DetailActivityData;
import edu.sjsu.hivo.events.MainActivityData;
import edu.sjsu.hivo.model.Property;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.adapter.CustomPagerAdapter;
import edu.sjsu.hivo.networking.VolleyNetwork;
;

public class PropertyDetail extends AppCompatActivity {
    private static final String TAG = PropertyDetail.class.getSimpleName();
    private RecyclerView recyclerView;
    private static final String POSITION_KEY = "POSITION";
    private PropertyDetailAdapter propertyDetailAdapter;
    ViewPager viewPager;
    private List<Double> list;
    private Property property;
    //private int position;
    CustomPagerAdapter adapter;
    Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_details);
        Log.i(TAG,"intoProperty DEtail");
        property = ((DetailActivityData)EventBus.getDefault().getStickyEvent(DetailActivityData.class)).getProperty();
        Log.i(TAG,"getting property object"+ property.getPrice());

        recyclerView = (RecyclerView)findViewById(R.id.property_details_rv);
        propertyDetailAdapter = new PropertyDetailAdapter(this, property);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(PropertyDetail.this, LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(verticalLayoutManager);
        recyclerView.setAdapter(propertyDetailAdapter);
        calculateEstimatedPrice(property);
    }

    public void calculateEstimatedPrice(final Property property){

       // Log.d(TAG, "inside sendRequestAndprintResponse()" + VolleyNetwork.AWS_ENDPOINT + extension+"&skip=**");
        final String url = "https://project-realestate.herokuapp.com/" +
                "price_prediction?zip="+property.getZip()+"&" +
                "beds="+property.getBeds()+"&baths="+property.getBaths()+"&sq_ft="+property.getArea()+"&" +
                "year_built="+property.getBuilt() + "&property_type=" + property.getPropertyType();
        Log.i(TAG, "price estimation url is "+ url);

        final JsonObjectRequest request = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "response is:" + response.toString());
                        try {
                            int price = response.getInt("PREDICTED_PRICE");
                            property.setPredictedPrice(String.valueOf(price));
                            propertyDetailAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            property.setPredictedPrice("Not Known");
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error == null || error.networkResponse == null) {
                            if (error != null) Log.e(TAG, "..", error);
                            return;
                        }

                        Log.e(TAG, "error making server request" + error.getMessage());
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(Request);

        VolleyNetwork
                .getInstance(getApplicationContext())
                .getRequestQueue(getApplicationContext())
                .add(request);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (property != null) {
            EventBus.getDefault().postSticky(new DetailActivityData(property));
        }
    }

}
