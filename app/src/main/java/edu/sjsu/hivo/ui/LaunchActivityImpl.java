package edu.sjsu.hivo.ui;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class LaunchActivityImpl implements LaunchActivityInterface {

    public String checkResponse(String response,String zipcode){
        String extension ="";
        if (!response.equals("")) {
            if (response.matches("[0-9]+")) {

                extension = "/zdata?zipcode=" + response;
            } else {
                try {
                    response = URLEncoder.encode(response, "UTF-8");
                    extension = "/data?str=" + response;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        else
            extension="/zdata?zipcode="+zipcode;
        return extension;
    }

    public String getZipcodeFromLocation(Location location, Context context){
        double currentLat = location.getLatitude();
        double currentLng = location.getLongitude();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String zipcode="";

        try {
            List<Address> addresses = geocoder.getFromLocation(currentLat, currentLng, 1);

            zipcode = addresses.get(0).getPostalCode();
//            zipcode = addresses.get(0).getPostalCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipcode;

    }
}
