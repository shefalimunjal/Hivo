package edu.sjsu.hivo.ui;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GeoCoderAccessor {
    Geocoder gc;
    ArrayList<Address> list;
    String TAG = GeoCoderAccessor.class.getSimpleName();
    public GeoCoderAccessor(Context context) {
        Object locale;
        gc = new Geocoder(context, Locale.getDefault());


    }

//    public List<Address> getList(String inputAddress) throws IOException{
//        if(gc.isPresent()){
//            list = (ArrayList<Address>) gc.getFromLocationName(inputAddress,5);
//        }
//        return list;
//    }

    public  double[] getList(String address)
    {
        double lat= 0.0, lng= 0.0;
        double[] location = new double[2];

        try
        {
            List<Address> addresses = gc.getFromLocationName(address , 1);
            Log.i(TAG, "address sent by user is......" + address);
            if (addresses.size() > 0)
            {
                location[0] =addresses.get(0).getLatitude();
                location[1] =addresses.get(0).getLongitude();

                Log.d("Latitude", ""+lat);
                Log.d("Longitude", ""+lng);
                return location;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }
}

