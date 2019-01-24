package edu.sjsu.hivo.ui.propertydetail;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import edu.sjsu.hivo.R;
import edu.sjsu.hivo.model.Property;

public class KeyDetailsViewHolder extends PropertyViewHolder {
    private TextView price_value_tv;
    private TextView beds_tv;
    private TextView beds_value_tv;
    private TextView baths_tv;
    private TextView baths_value_tv;
    private TextView address_line_1_tv;
    private TextView address_line_2_tv;
    private TextView area_tv;
    private TextView area_value_tv;
    private TextView lot_size_tv;
    private TextView lot_size_value_tv;
    private TextView key_details_tv;
    private TextView property_type_tv;
    private TextView property_type_value_tv;
    private TextView build_year_tv;
    private TextView build_year_value_tv;
    private TextView price_per_sq_ft_tv;
    private TextView price_per_sq_ft_value_tv;
    private TextView status_tv;
    private TextView status_value_tv;
    //private TextView estimatedPrice;

    public KeyDetailsViewHolder(View view) {
        super(view);
        price_value_tv = (TextView)view.findViewById(R.id.property_price_tv);
        beds_tv = (TextView)view.findViewById(R.id.property_beds_tv);
        beds_value_tv = (TextView)view.findViewById(R.id.property_bed_no_tv);
        baths_tv = (TextView)view.findViewById(R.id.property_baths_tv);
        baths_value_tv = (TextView)view.findViewById(R.id.property_baths_no_tv);
        address_line_1_tv = (TextView)view.findViewById(R.id.property_address_line1_tv);
        address_line_2_tv = (TextView)view.findViewById(R.id.property_address_line2_tv);
        area_tv = (TextView)view.findViewById(R.id.sq_ft);
        area_value_tv = (TextView)view.findViewById(R.id.sq_ft_value);
        lot_size_tv = (TextView)view.findViewById(R.id.lot_size);
        lot_size_value_tv = (TextView)view.findViewById(R.id.lot_size_value);
        key_details_tv = (TextView)view.findViewById(R.id.key_details);
        property_type_tv = (TextView)view.findViewById(R.id.property_type);
        property_type_value_tv = (TextView)view.findViewById(R.id.property_type_value);
        build_year_tv = (TextView)view.findViewById(R.id.built_year);
        build_year_value_tv = (TextView)view.findViewById(R.id.built_year_value);
        price_per_sq_ft_tv = (TextView)view.findViewById(R.id.price_per_sq_ft);
        price_per_sq_ft_value_tv = (TextView)view.findViewById(R.id.price_per_sq_ft_value);
        status_tv = (TextView)view.findViewById(R.id.status_tv);
        status_value_tv = (TextView)view.findViewById(R.id.status_value);
       // estimatedPrice = (TextView)view.findViewById(R.id.key_details);



    }

    @Override
    public void bindProperty(Property property) {

        price_value_tv.setText(property.getPrice());
        address_line_1_tv.setText(property.getAddress());
        address_line_2_tv.setText(property.getAddress2());
        beds_value_tv.setText(property.getBeds());
        baths_value_tv.setText(property.getBaths());
        area_value_tv.setText(property.getArea());
        lot_size_value_tv.setText(property.getLotSize());
        property_type_value_tv.setText(property.getPropertyType());
        build_year_value_tv.setText(property.getBuilt());
        status_value_tv.setText(property.getSaleType());
        price_per_sq_ft_value_tv.setText(property.getPricePerSqFt());
        if(property.getPredictedPrice() != null){
            key_details_tv.setText(property.getPredictedPrice());
        }
    }
}
