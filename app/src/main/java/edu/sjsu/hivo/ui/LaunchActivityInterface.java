package edu.sjsu.hivo.ui;


import android.content.Context;
import android.location.Location;

public interface LaunchActivityInterface {
    String checkResponse(String response,String zipcode);
    String getZipcodeFromLocation(Location location, Context context);
}
