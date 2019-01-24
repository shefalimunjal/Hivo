package edu.sjsu.hivo.model;

import android.graphics.drawable.Drawable;



public class UnUsed {
    String property_price_tv;
    String property_address_line1_tv;
    String property_address_line2_tv;
    String property_bed_no_tv;
    String property_bath_no_tv;
    String property_sqft_no_tv;

    public UnUsed(){
        property_price_tv = "$3,00,000";
        property_address_line1_tv="2781 Mauricia Ave, Unit B";
        property_address_line2_tv = "Santa Clara, CA, 95051";
        property_bed_no_tv = "2";
        property_bath_no_tv = "2";
        property_sqft_no_tv= "1,380";
    }

    public String getProperty_address_line1_tv() {
        return property_address_line1_tv;
    }

    public String getProperty_address_line2_tv() {
        return property_address_line2_tv;
    }

    public String getProperty_bath_no_tv() {
        return property_bath_no_tv;
    }

    public String getProperty_bed_no_tv() {
        return property_bed_no_tv;
    }

    public String getProperty_price_tv() {
        return property_price_tv;
    }

    public String getProperty_sqft_no_tv() {
        return property_sqft_no_tv;
    }
}
